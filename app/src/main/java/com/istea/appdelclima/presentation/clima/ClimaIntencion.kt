package com.istea.appdelclima.presentation.clima

import com.istea.appdelclima.repository.modelos.Ciudad

sealed interface ClimaIntencion {
    data class Cargar(val ciudad: Ciudad) : ClimaIntencion
}
