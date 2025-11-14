package com.example.parcial2_climapp.repository

import com.example.parcial2_climapp.repository.modelos.*

interface Repositorio {
    suspend fun buscarCiudades(q: String): List<Ciudad>
    suspend fun climaActual(lat: Double, lon: Double): ClimaActual
    suspend fun pronostico(lat: Double, lon: Double): Pronostico
}
