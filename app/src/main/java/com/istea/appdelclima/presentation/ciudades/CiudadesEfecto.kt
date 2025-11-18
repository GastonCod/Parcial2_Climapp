package com.istea.appdelclima.presentation.ciudades

import com.istea.appdelclima.repository.modelos.Ciudad

sealed interface CiudadesEfecto {
    data class NavegarAClima(val ciudad: Ciudad) : CiudadesEfecto
}
