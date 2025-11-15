package com.example.parcial2_climapp.presentation.clima

import com.example.parcial2_climapp.repository.modelos.*

data class ClimaEstado(
    val ciudad: Ciudad? = null,
    val cargando: Boolean = false,
    val hoy: ClimaActual? = null,
    val dias: Pronostico = emptyList(),
    val error: String? = null
)
