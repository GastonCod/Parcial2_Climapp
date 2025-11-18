package com.example.parcial2_climapp

import android.content.ContextWrapper
import com.istea.appdelclima.presentation.ciudades.CiudadesEstado
import com.istea.appdelclima.presentation.ciudades.CiudadesViewModel
import com.istea.appdelclima.repository.Repositorio
import com.istea.appdelclima.repository.RepositorioMock
import com.istea.appdelclima.repository.RepositorioPreferencias
import com.istea.appdelclima.repository.modelos.Ciudad
import com.istea.appdelclima.repository.modelos.ClimaActual
import com.istea.appdelclima.repository.modelos.Pronostico
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
class CiudadesViewModelTest {
    // Simula el hilo Main de Android
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    // Repositorio simulado que devuelve ciudades
    private val repo = RepositorioMock()
    // Preferencias falsas (no guarda nada realmente)
    private val prefs = RepositorioPreferencias(FakeContext())
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
    @Test
    fun buscar_cor_devuelveCordoba() = runTest(timeout = 3.seconds) {
        val viewModel = CiudadesViewModel(repo, prefs)
        val estadoEsperado = CiudadesEstado(
            query = "Cór",
            cargando = false,
            resultados = listOf(
                Ciudad("Córdoba", -31.4, -64.18, "AR")
            ),
            error = null
        )
        launch(Dispatchers.Main) {
            viewModel.onQueryChange("Cór")
            delay(1000) // VM (250) + repo (250) + margen
            assertEquals(estadoEsperado, viewModel.estado.value)
        }
    }
    @Test
    fun buscar_sinResultados_devuelveListaVacia() = runTest(timeout = 3.seconds) {
        val viewModel = CiudadesViewModel(repo, prefs)
        val estadoEsperado = CiudadesEstado(
            query = "xyz",
            cargando = false,
            resultados = emptyList(),
            error = null
        )
        launch(Dispatchers.Main) {
            viewModel.onQueryChange("xyz")
            delay(800)
            assertEquals(estadoEsperado, viewModel.estado.value)
        }
    }
    @Test
    fun buscar_error_actualizaEstadoError() = runTest(timeout = 3.seconds) {
        val repoError = RepositorioErrorMock()
        val viewModel = CiudadesViewModel(repoError, prefs)
        launch(Dispatchers.Main) {
            viewModel.onQueryChange("cualquier")
            delay(800)
            val estado = viewModel.estado.value
            assertEquals("Fallo en la búsqueda", estado.error)
        }
    }
}
/**
 * Contexto falso: permite instanciar RepositorioPreferencias
 * sin usar un Context real de Android.
 */
class FakeContext : ContextWrapper(null)
/**
 * Repositorio que simula siempre un error al buscar.
 */
class RepositorioErrorMock : Repositorio {
    override suspend fun buscarCiudades(q: String): List<Ciudad> {
        throw RuntimeException("Fallo en la búsqueda")
    }
    override suspend fun climaActual(lat: Double, lon: Double): ClimaActual {
        throw NotImplementedError()
    }
    override suspend fun pronostico(lat: Double, lon: Double): Pronostico {
        throw NotImplementedError()
    }
    override suspend fun ciudadPorUbicacion(lat: Double, lon: Double): Ciudad? {
        return null
    }
}