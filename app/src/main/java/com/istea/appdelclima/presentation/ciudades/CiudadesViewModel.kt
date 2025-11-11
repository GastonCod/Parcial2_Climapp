package com.istea.appdelclima.presentation.ciudades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istea.appdelclima.repository.Repositorio
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CiudadesViewModel(private val repo: Repositorio) : ViewModel() {
    var estado = androidx.compose.runtime.mutableStateOf(CiudadesEstado())
        private set

    private var job: Job? = null

    fun onQueryChange(q: String) {
        estado.value = estado.value.copy(query = q, cargando = true)
        job?.cancel()
        job = viewModelScope.launch {
            delay(250)
            runCatching { repo.buscarCiudades(q) }
                .onSuccess { estado.value = estado.value.copy(cargando = false, resultados = it, error = null) }
                .onFailure { estado.value = estado.value.copy(cargando = false, error = it.message) }
        }
    }
}
