package com.istea.appdelclima.presentation.clima

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istea.appdelclima.repository.Repositorio
import com.istea.appdelclima.repository.modelos.Ciudad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClimaViewModel(
    private val repo: Repositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(ClimaEstado())
    val estado: StateFlow<ClimaEstado> = _estado.asStateFlow()

    fun dispatch(intencion: ClimaIntencion) {
        when (intencion) {
            is ClimaIntencion.Cargar -> cargarPara(intencion.ciudad)
        }
    }

    private fun cargarPara(ciudad: Ciudad) {
        _estado.value = ClimaEstado(ciudad = ciudad, cargando = true)

        viewModelScope.launch {
            try {
                val now = repo.climaActual(ciudad.lat, ciudad.lon)
                val list = repo.pronostico(ciudad.lat, ciudad.lon)

                _estado.update {
                    it.copy(
                        cargando = false,
                        hoy = now,
                        dias = list,
                        ciudad = ciudad,
                        error = null
                    )
                }
                println(">>> Clima cargado OK")

            } catch (e: Exception) {
                e.printStackTrace()
                println(">>> ERROR CARGANDO CLIMA: ${e.message}")

                _estado.update {
                    it.copy(
                        cargando = false, // 👈 NECESARIO PARA QUE NO SE BLOQUEE LA UI
                        error = e.message ?: "Error cargando clima"
                    )
                }
            }
        }
    }
}
