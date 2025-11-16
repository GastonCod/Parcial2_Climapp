package com.example.parcial2_climapp.presentation.clima

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parcial2_climapp.repository.modelos.Ciudad

@Composable
fun ClimaView(vm: ClimaViewModel, onCambiarCiudad: () -> Unit) {
    val s by vm.estado
    val context = LocalContext.current

    // Efecto para cargar los datos cuando la ciudad en el estado cambia
    LaunchedEffect(s.ciudad) {
        s.ciudad?.let { vm.cargarPara(it) }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(s.ciudad?.name ?: "", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.weight(1f)) // Empuja los botones a la derecha

            // Botón para compartir
            IconButton(onClick = {
                val textoParaCompartir = vm.prepararTextoParaCompartir()
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textoParaCompartir)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir Pronóstico"
                )
            }

            TextButton(onClick = onCambiarCiudad) { Text("Cambiar ciudad") }
        }
        if (s.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
        s.hoy?.let { Text("Ahora: ${it.temperatura}°C • ${it.estado} • Hum ${it.humedad}%\n", Modifier.padding(top = 8.dp)) }
        s.dias.forEach { d -> Text("• ${d.dia}: ${d.tempMin}°/${d.tempMax}°  ${d.estado}") }
        s.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }

        // Gráfico de Temperaturas
        if (s.grafico.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            GraficoTemperaturas(puntos = s.grafico, modifier = Modifier
                .fillMaxWidth()
                .height(120.dp))
        }
    }
}

@Composable
fun GraficoTemperaturas(puntos: List<PuntoGrafico>, modifier: Modifier = Modifier) {
    val maxTempGeneral = puntos.maxOf { it.tempMax } + 2
    val minTempGeneral = puntos.minOf { it.tempMin } - 2
    val rango = maxTempGeneral - minTempGeneral

    // Colores del tema
    val colorTexto = MaterialTheme.colorScheme.onSurface
    val colorBarras = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) { 
        val anchoBarra = size.width / (puntos.size * 2)
        val textPaint = android.graphics.Paint().apply { 
            color = colorTexto.toArgb()
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }

        puntos.forEachIndexed { i, punto ->
            val x = (i * 2 + 1) * anchoBarra

            val yMax = ((maxTempGeneral - punto.tempMax) / rango) * size.height
            val yMin = ((maxTempGeneral - punto.tempMin) / rango) * size.height

            // Dibuja la barra de temperatura
            drawRect(
                color = colorBarras,
                topLeft = Offset(x - anchoBarra / 2, yMax),
                size = Size(anchoBarra, yMin - yMax)
            )
            
            // Dibuja el día de la semana
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(punto.dia, x, size.height - 5, textPaint)
            }
        }
    }
}