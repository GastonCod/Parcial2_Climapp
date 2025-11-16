package com.example.parcial2_climapp.presentation.clima

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2_climapp.repository.Repositorio
import com.example.parcial2_climapp.repository.modelos.Ciudad
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                val graficoData = list.map {
                    PuntoGrafico(
                        dia = formatearDia(it.dia),
                        tempMax = it.tempMax.toFloat(),
                        tempMin = it.tempMin.toFloat()
                    )
                }
                estado.value = estado.value.copy(
                    cargando = false,
                    hoy = now,
                    dias = list,
                    grafico = graficoData
                )
            }.onFailure {
                estado.value = estado.value.copy(cargando = false, error = it.message)
            }
        }
    }

    private fun formatearDia(fecha: String): String {
        return try {
            val localDate = LocalDate.parse(fecha)
            val formatter = DateTimeFormatter.ofPattern("EEE")
            localDate.format(formatter).uppercase()
        } catch (e: Exception) {
            fecha // Devolver la fecha original si hay un error de formato
        }
    }

    fun prepararTextoParaCompartir(): String {
        val estadoActual = estado.value
        val ciudad = estadoActual.ciudad?.name ?: "una ciudad"
        val climaHoy = estadoActual.hoy
        if (climaHoy == null) {
            return "No hay datos del clima para compartir."
        }
        return "¡El clima en ${ciudad}! Temperatura actual: ${climaHoy.temperatura}°C y ${climaHoy.estado.lowercase()}."
    }
}