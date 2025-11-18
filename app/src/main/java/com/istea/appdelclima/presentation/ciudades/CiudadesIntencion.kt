package com.istea.appdelclima.presentation.ciudades

import com.istea.appdelclima.repository.modelos.Ciudad

sealed interface CiudadesIntencion {
    data class Buscar(val query: String) : CiudadesIntencion
    data object ClickGeo : CiudadesIntencion
    data class CiudadClick(val ciudad: Ciudad) : CiudadesIntencion
    data object ErrorMostrado : CiudadesIntencion
}
