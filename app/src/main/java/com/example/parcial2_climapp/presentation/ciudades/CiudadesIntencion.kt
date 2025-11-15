package com.example.parcial2_climapp.presentation.ciudades

sealed interface CiudadesIntencion {
    data class Buscar(val q: String) : CiudadesIntencion
}
