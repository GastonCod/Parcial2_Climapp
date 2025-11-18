package com.istea.appdelclima

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.istea.appdelclima.presentation.ciudades.CiudadesView
import com.istea.appdelclima.presentation.ciudades.CiudadesViewModel
import com.istea.appdelclima.presentation.ciudades.ProvideContextToVM
import com.istea.appdelclima.presentation.clima.ClimaView
import com.istea.appdelclima.presentation.clima.ClimaViewModel
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
                    val nav = rememberNavController()
                    val repo = RepositorioApi()
                    val repositorioPreferencias = RepositorioPreferencias(this)

                    val vmCities = remember { CiudadesViewModel(repo, repositorioPreferencias) }
                    val vmWeather = remember { ClimaViewModel(repo) }

                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        val ciudadGuardada = repositorioPreferencias.getCiudadGuardada().first()
                        if (ciudadGuardada != null) {
                            val ciudades = repo.buscarCiudades(ciudadGuardada)
                            if (ciudades.isNotEmpty()) {
                                vmWeather.cargarPara(ciudades.first())
                                startDestination = "clima"
                            } else {
                                startDestination = "ciudades"
                            }
                        } else {
                            startDestination = "ciudades"
                        }
                    }

                    if (startDestination != null) {
                        NavHost(navController = nav, startDestination = startDestination!!) {
                            composable("ciudades") {
                                ProvideContextToVM(vmCities)

                                CiudadesView(vmCities) { ciudad: Ciudad ->
                                    vmCities.onCiudadSeleccionada(ciudad)
                                    vmWeather.cargarPara(ciudad)
                                    nav.navigate("clima")
                                }
                            }

                            composable("clima") {
                                ClimaView(vmWeather) {
                                    nav.navigate("ciudades") { popUpTo(0) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                println(">>> Permisos de ubicación concedidos")
            } else {
                println(">>> Permisos de ubicación denegados")
            }
        }
    }
}