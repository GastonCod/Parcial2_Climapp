package com.istea.appdelclima.repository

import com.istea.appdelclima.repository.modelos.*
import kotlinx.coroutines.delay

class RepositorioMock : Repositorio {
    private val base = listOf(
        Ciudad("Córdoba", -31.4, -64.18, "AR"),
        Ciudad("Buenos Aires", -34.61, -58.38, "AR"),
        Ciudad("Mendoza", -32.89, -68.83, "AR")
    )

    override suspend fun buscarCiudades(q: String): List<Ciudad> {
        delay(250)
        return if (q.isBlank()) emptyList()
        else base.filter { it.name.contains(q, ignoreCase = true) }
    }

    override suspend fun climaActual(lat: Double, lon: Double): ClimaActual {
        delay(250)
        return ClimaActual("Ciudad seleccionada", 24.5, 55, "Clear")
    }

    override suspend fun pronostico(lat: Double, lon: Double): Pronostico {
        delay(250)
        return listOf(
            PronosticoDia("Hoy",     18.0, 26.0, "Clear"),
            PronosticoDia("Mañana",  17.0, 25.0, "Clouds"),
            PronosticoDia("Pasado",  16.0, 24.0, "Rain")
        )
    }
}
