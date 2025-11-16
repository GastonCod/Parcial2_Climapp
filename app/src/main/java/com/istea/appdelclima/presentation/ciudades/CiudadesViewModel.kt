package com.istea.appdelclima.presentation.ciudades

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istea.appdelclima.repository.Repositorio
import com.istea.appdelclima.repository.modelos.Ciudad
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CiudadesViewModel(
    val repo: Repositorio
) : ViewModel() {

    var estado = androidx.compose.runtime.mutableStateOf(CiudadesEstado())
        private set

    @SuppressLint("StaticFieldLeak")
    var currentContext: Context? = null

    private var job: Job? = null

    fun onQueryChange(q: String) {
        estado.value = estado.value.copy(query = q, cargando = true)
        job?.cancel()
        job = viewModelScope.launch {
            delay(250)
            runCatching { repo.buscarCiudades(q) }
                .onSuccess {
                    estado.value = estado.value.copy(
                        cargando = false,
                        resultados = it,
                        error = null
                    )
                }
                .onFailure { e ->
                    estado.value = estado.value.copy(
                        cargando = false,
                        error = e.message ?: "Error buscando ciudades"
                    )
                }
        }
    }

    fun onClickGeo(
        onSeleccionar: (Ciudad) -> Unit
    ) {
        println(">>> onClickGeo ejecutado")

        val ctx = currentContext
        println(">>> Context = $ctx")

        if (ctx == null) {
            estado.value = estado.value.copy(
                error = "No hay contexto para leer ubicación"
            )
            return
        }

        obtenerUbicacion(ctx) { lat, lon ->
            viewModelScope.launch {
                try {
                    val ciudad = repo.ciudadPorUbicacion(lat, lon)

                    if (ciudad != null) {
                        onSeleccionar(ciudad)
                    } else {
                        estado.value = estado.value.copy(
                            error = "No se encontró ciudad para tu ubicación"
                        )
                    }
                } catch (e: Exception) {
                    estado.value = estado.value.copy(
                        error = "Error al leer la ubicación del dispositivo: ${e.message}"
                    )
                }
            }
        }
    }
}