package com.istea.appdelclima

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.istea.appdelclima.presentation.ciudades.*
import com.istea.appdelclima.presentation.clima.*
import com.istea.appdelclima.repository.RepositorioApi
import com.istea.appdelclima.repository.RepositorioPreferencias
import com.istea.appdelclima.repository.modelos.Ciudad
import com.istea.appdelclima.ui.theme.AppDelClimaTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirPermisosUbicacion()

        setContent {
            AppDelClimaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    // NAVIGATION
                    val nav = rememberNavController()

                    // REPOS
                    val repo = remember { RepositorioApi() }
                    val prefs = remember { RepositorioPreferencias(this) }

                    // VIEWMODELS MVI
                    val vmCities = remember { CiudadesViewModel(repo, prefs) }
                    val vmWeather = remember { ClimaViewModel(repo) }

                    // START DESTINATION CONTROL
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    // CARGA INICIAL
                    LaunchedEffect(Unit) {
                        val ciudadGuardada = prefs.getCiudadGuardada().first()

                        if (ciudadGuardada != null) {
                            val ciudadesEncontradas = repo.buscarCiudades(ciudadGuardada)

                            if (ciudadesEncontradas.isNotEmpty()) {
                                // MVI-style dispatch
                                vmWeather.dispatch(
                                    ClimaIntencion.Cargar(ciudadesEncontradas.first())
                                )
                                startDestination = "clima"
                            } else {
                                startDestination = "ciudades"
                            }
                        } else {
                            startDestination = "ciudades"
                        }
                    }

                    if (startDestination != null) {

                        NavHost(
                            navController = nav,
                            startDestination = startDestination!!
                        ) {

                            // ----------------------------------------------------------
                            // CIUDADES
                            // ----------------------------------------------------------
                            composable("ciudades") {

                                ProvideContextToVM(vmCities)

                                CiudadesView(
                                    vm = vmCities,
                                    onSeleccionar = { ciudad ->
                                        vmWeather.dispatch(ClimaIntencion.Cargar(ciudad))
                                        nav.navigate("clima")
                                    }
                                )
                            }

                            // ----------------------------------------------------------
                            // CLIMA
                            // ----------------------------------------------------------
                            composable("clima") {

                                ClimaView(
                                    vm = vmWeather,
                                    onCambiarCiudad = {
                                        nav.navigate("ciudades") {
                                            popUpTo(0)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ----------------------------------------------------------
    // PERMISOS
    // ----------------------------------------------------------

    private fun pedirPermisosUbicacion() {
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val algunoNoOtorgado = permisos.any { permiso ->
            ActivityCompat.checkSelfPermission(
                this,
                permiso
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (algunoNoOtorgado) {
            ActivityCompat.requestPermissions(this, permisos, 2001)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2001) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                println(">>> Permisos de ubicación concedidos")
            } else {
                println(">>> Permisos de ubicación denegados")
            }
        }
    }
}