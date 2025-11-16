package com.example.parcial2_climapp.repository
import com.example.parcial2_climapp.BuildConfig
import com.example.parcial2_climapp.repository.modelos.Ciudad
import com.example.parcial2_climapp.repository.modelos.ClimaActual
import com.example.parcial2_climapp.repository.modelos.Pronostico
import com.example.parcial2_climapp.repository.modelos.PronosticoDia
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId

class RepositorioApi : Repositorio {

    private val apiKey = BuildConfig.OWM_KEY
    private val units = "metric"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        // logs de red en Logcat
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
        // errores HTTP explícitos
        HttpResponseValidator {
            validateResponse { r ->
                if (r.status.value >= 400) {
                    Log.e("OWM", "HTTP ${r.status.value} ${r.status.description}")
                    error("HTTP ${r.status.value} ${r.status.description}")
                }
            }
        }
    }

    // ---------------- DTOs de red ----------------
    @Serializable private data class GeoCityDto(val name: String, val lat: Double, val lon: Double, val country: String? = null)
    @Serializable private data class Main(val temp: Double, val humidity: Int)
    @Serializable private data class Wx(val main: String, val description: String, val icon: String)
    @Serializable private data class CurrentDto(val name: String, val dt: Long, val main: Main, val weather: List<Wx>)
    @Serializable private data class ForecastItem(val dt: Long, val main: Main, val weather: List<Wx>)
    @Serializable private data class ForecastDto(val list: List<ForecastItem>)

    // --- Implementación
    override suspend fun buscarCiudades(q: String): List<Ciudad> {
        if (q.isBlank()) return emptyList()
        val resp = client.get("https://api.openweathermap.org/geo/1.0/direct") {
            parameter("q", q)
            parameter("limit", 10)
            parameter("appid", apiKey)
        }
        Log.d("OWM", "geocoding status=${resp.status}")
        val list: List<GeoCityDto> = resp.body()
        return list.map { Ciudad(it.name, it.lat, it.lon, it.country) }
    }


    override suspend fun climaActual(lat: Double, lon: Double): ClimaActual {
        val resp = client.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", units)
            parameter("appid", apiKey)
        }
        Log.d("OWM", "current status=${resp.status}")
        val dto: CurrentDto = resp.body()
        return ClimaActual(
            ciudad = dto.name,
            temperatura = dto.main.temp,
            humedad = dto.main.humidity,
            estado = dto.weather.firstOrNull()?.main.orEmpty()
        )
    }


    override suspend fun pronostico(lat: Double, lon: Double): Pronostico {
        val resp = client.get("https://api.openweathermap.org/data/2.5/forecast") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", units)
            parameter("appid", apiKey)
        }

        Log.d("OWM", "forecast status=${resp.status}")

        val dto: ForecastDto = resp.body()

        val byDay = dto.list.groupBy {
            Instant.ofEpochSecond(it.dt).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        return byDay.entries.sortedBy { it.key }.map { (date, items) ->
            val min = items.minOf { it.main.temp }
            val max = items.maxOf { it.main.temp }
            val estado = items.map { it.weather.firstOrNull()?.main.orEmpty() }
                .groupingBy { it }.eachCount().maxBy { it.value }.key

            PronosticoDia(
                dia = date.toString(),
                tempMin = min,
                tempMax = max,
                estado = estado
            )
        }
    }
}
