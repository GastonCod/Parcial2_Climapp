package com.istea.appdelclima.presentation.ciudades

import com.istea.appdelclima.repository.modelos.Ciudad

data class CiudadesEstado(
    val query: String = "",
    val cargando: Boolean = false,
    val resultados: List<Ciudad> = emptyList(),
    val error: String? = null
)
