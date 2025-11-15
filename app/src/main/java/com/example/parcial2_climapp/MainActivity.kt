package com.example.parcial2_climapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial2_climapp.presentation.ciudades.*
import com.example.parcial2_climapp.presentation.clima.*
import com.example.parcial2_climapp.repository.RepositorioApi
import com.example.parcial2_climapp.repository.RepositorioMock
import com.example.parcial2_climapp.repository.modelos.Ciudad
import com.example.parcial2_climapp.ui.theme.AppDelClimaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.e("API_KEY_TEST", "KEY = ${BuildConfig.OWM_KEY}"

        setContent {
            AppDelClimaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val nav = rememberNavController()
                    val repo = RepositorioApi()

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

//.