package com.example.parcial2_climapp.presentation.ciudades

import com.example.parcial2_climapp.repository.modelos.Ciudad

data class CiudadesEstado(
    val query: String = "",
    val cargando: Boolean = false,
    val resultados: List<Ciudad> = emptyList(),
    val error: String? = null
)