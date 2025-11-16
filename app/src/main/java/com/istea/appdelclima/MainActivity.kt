package com.istea.appdelclima

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.istea.appdelclima.presentation.ciudades.*
import com.istea.appdelclima.presentation.clima.*
import com.istea.appdelclima.repository.RepositorioApi
import com.istea.appdelclima.repository.modelos.Ciudad
import com.istea.appdelclima.ui.theme.AppDelClimaTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirPermisosUbicacion()

        setContent {
            AppDelClimaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    val nav = rememberNavController()
                    val repo = RepositorioApi()

                    val vmCities = remember { CiudadesViewModel(repo) }
                    val vmWeather = remember { ClimaViewModel(repo) }

                    NavHost(navController = nav, startDestination = "ciudades") {

                        composable("ciudades") {
                            ProvideContextToVM(vmCities)

                            CiudadesView(vmCities) { ciudad: Ciudad ->
                                vmWeather.cargarPara(ciudad)
                                nav.navigate("clima")
                            }
                        }

                        composable("clima") {
                            ClimaView(vmWeather) {
                                nav.popBackStack()
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