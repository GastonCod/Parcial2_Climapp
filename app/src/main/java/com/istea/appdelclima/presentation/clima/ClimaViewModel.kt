package com.istea.appdelclima.presentation.clima

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istea.appdelclima.repository.Repositorio
import com.istea.appdelclima.repository.modelos.Ciudad
import kotlinx.coroutines.launch

class ClimaViewModel(private val repo: Repositorio) : ViewModel() {
    var estado = androidx.compose.runtime.mutableStateOf(ClimaEstado())
        private set

    fun cargarPara(ciudad: Ciudad) {
        estado.value = ClimaEstado(ciudad = ciudad, cargando = true)
        viewModelScope.launch {
            runCatching {
                val now = repo.climaActual(ciudad.lat, ciudad.lon)
                val list = repo.pronostico(ciudad.lat, ciudad.lon)
                now to list
            }.onSuccess { (now, list) ->
                estado.value = estado.value.copy(cargando = false, hoy = now, dias = list)
            }.onFailure {
                estado.value = estado.value.copy(cargando = false, error = it.message)
            }
        }
    }
}
