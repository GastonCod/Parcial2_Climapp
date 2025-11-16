package com.istea.appdelclima.repository.modelos

data class ClimaActual(
    val ciudad: String,
    val temperatura: Double,
    val humedad: Int,
    val estado: String
)

data class PronosticoDia(
    val dia: String,
    val tempMin: Double,
    val tempMax: Double,
    val estado: String
)
