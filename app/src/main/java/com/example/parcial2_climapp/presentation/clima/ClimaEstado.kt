package com.example.parcial2_climapp.presentation.clima

import com.example.parcial2_climapp.repository.modelos.*

// Estructura de datos para un punto en el gráfico
data class PuntoGrafico(
    val dia: String,
    val tempMax: Float,
    val tempMin: Float,
)

data class ClimaEstado(
    val ciudad: Ciudad? = null,
    val cargando: Boolean = false,
    val hoy: ClimaActual? = null,
    val dias: Pronostico = emptyList(),
    val grafico: List<PuntoGrafico> = emptyList(), // Lista de puntos para el gráfico
    val error: String? = null
)
