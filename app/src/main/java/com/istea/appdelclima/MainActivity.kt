package com.istea.appdelclima

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.istea.appdelclima.presentation.ciudades.*
import com.istea.appdelclima.presentation.clima.*
import com.istea.appdelclima.repository.RepositorioMock
import com.istea.appdelclima.repository.modelos.Ciudad
import com.istea.appdelclima.ui.theme.AppDelClimaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppDelClimaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val nav = rememberNavController()
                    val repo = RepositorioMock()

                    val vmCities = remember { CiudadesViewModel(repo) }
                    val vmWeather = remember { ClimaViewModel(repo) }

                    NavHost(navController = nav, startDestination = "ciudades") {
                        composable("ciudades") {
                            CiudadesView(vmCities) { ciudad: Ciudad ->
                                vmWeather.cargarPara(ciudad)
                                nav.navigate("clima")
                            }
                        }
                        composable("clima") {
                            ClimaView(vmWeather) { nav.popBackStack() }
                        }
                    }
                }
            }
        }
    }
}
//Finalizamos la parte de la api