package com.istea.appdelclima.presentation.clima

import com.istea.appdelclima.repository.modelos.*

data class ClimaEstado(
    val ciudad: Ciudadd? = null,
    val cargando: Boolean = false,
    val hoy: ClimaActual? = null,
    val dias: Pronostico = emptyList(),
    val error: String? = null
)
