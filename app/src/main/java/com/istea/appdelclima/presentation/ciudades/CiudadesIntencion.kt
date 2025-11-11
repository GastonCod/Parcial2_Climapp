package com.istea.appdelclima.presentation.ciudades
sealed interface CiudadesIntencion {
    data class Buscar(val q: String) : CiudadesIntencion
}
