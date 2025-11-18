package com.istea.appdelclima.presentation.ciudades

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istea.appdelclima.repository.Repositorio
import com.istea.appdelclima.repository.RepositorioPreferencias
import com.istea.appdelclima.repository.modelos.Ciudad
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CiudadesViewModel(
    private val repo: Repositorio,
    private val repositorioPreferencias: RepositorioPreferencias
) : ViewModel() {

    // STATE
    private val _estado = MutableStateFlow(CiudadesEstado())
    val estado: StateFlow<CiudadesEstado> = _estado.asStateFlow()

    // EFFECTS
    private val _efectos = MutableSharedFlow<CiudadesEfecto>()
    val efectos: SharedFlow<CiudadesEfecto> = _efectos.asSharedFlow()

    @SuppressLint("StaticFieldLeak")
    var currentContext: Context? = null

    private var job: Job? = null

    fun dispatch(intencion: CiudadesIntencion) {
        when (intencion) {
            is CiudadesIntencion.Buscar      -> manejarBusqueda(intencion.query)
            CiudadesIntencion.ClickGeo       -> manejarGeo()
            is CiudadesIntencion.CiudadClick -> manejarCiudadClick(intencion.ciudad)
            CiudadesIntencion.ErrorMostrado  -> limpiarError()
        }
    }

    private fun manejarBusqueda(q: String) {
        _estado.update { it.copy(query = q, cargando = true, error = null) }

        job?.cancel()
        job = viewModelScope.launch {
            delay(250)

            runCatching { repo.buscarCiudades(q) }
                .onSuccess { ciudades ->
                    _estado.update {
                        it.copy(
                            cargando = false,
                            resultados = ciudades,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = e.message ?: "Error buscando ciudades"
                        )
                    }
                }
        }
    }

    private fun manejarCiudadClick(ciudad: Ciudad) {
        viewModelScope.launch {
            repositorioPreferencias.guardarCiudad(ciudad.name)

            _efectos.emit(CiudadesEfecto.NavegarAClima(ciudad))
        }
    }

    private fun manejarGeo() {
        val ctx = currentContext
        if (ctx == null) {
            _estado.update { it.copy(error = "No hay contexto para leer ubicación") }
            return
        }

        obtenerUbicacion(ctx) { lat, lon ->
            viewModelScope.launch {
                try {
                    val ciudad = repo.ciudadPorUbicacion(lat, lon)

                    if (ciudad != null) {
                        manejarCiudadClick(ciudad)
                    } else {
                        _estado.update {
                            it.copy(error = "No se encontró ciudad para tu ubicación")
                        }
                    }
                } catch (e: Exception) {
                    _estado.update {
                        it.copy(
                            error = "Error al leer la ubicación del dispositivo: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    private fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }
}