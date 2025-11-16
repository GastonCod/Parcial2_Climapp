package com.istea.appdelclima.repository

import com.istea.appdelclima.repository.modelos.*

interface Repositorio {
    suspend fun buscarCiudades(q: String): List<Ciudad>
    suspend fun climaActual(lat: Double, lon: Double): ClimaActual
    suspend fun pronostico(lat: Double, lon: Double): Pronostico
    suspend fun ciudadPorUbicacion(lat: Double, lon: Double): Ciudad?
}
