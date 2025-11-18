package com.istea.appdelclima.presentation.clima

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.istea.appdelclima.repository.modelos.Ciudad
import com.istea.appdelclima.repository.modelos.ClimaActual
import com.istea.appdelclima.repository.modelos.PronosticoDia

@Composable
fun ClimaView(vm: ClimaViewModel, onCambiarCiudad: () -> Unit) {
    val s by vm.estado
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- BARRA SUPERIOR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                s.ciudad?.country?.let { countryCode ->
                    AsyncImage(
                        model = "https://flagcdn.com/w80/${countryCode.lowercase()}.png",
                        contentDescription = "Bandera de ${s.ciudad?.name}",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = s.ciudad?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = onCambiarCiudad) { Text("Cambiar") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- INDICADOR DE CARGA ---
        if (s.cargando) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }

        // --- TARJETA DE CLIMA ACTUAL ---
        s.hoy?.let { climaActual ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Estado y Temperatura
                    Text(
                        text = climaActual.estado.replaceFirstChar { it.uppercase() },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${climaActual.temperatura}°C",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Humedad
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = "Humedad",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${climaActual.humedad}% Humedad",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    // Botón de Compartir
                    FilledTonalButton(onClick = { compartirPronostico(context, s.ciudad, s.hoy) }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Compartir")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir")
                    }
                }
            }
        }

        // --- PRONÓSTICO DIARIO ---
        if(s.dias.isNotEmpty()){
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Próximos días",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(s.dias) { dia ->
                    PronosticoItem(dia = dia)
                }
            }
        }
        
        // --- MENSAJE DE ERROR ---
        s.error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun PronosticoItem(dia: PronosticoDia) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = dia.dia, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(
                text = dia.estado.replaceFirstChar { it.uppercase() },
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                 modifier = Modifier.weight(1f)
            )
            Text(
                text = "${dia.tempMax}° / ${dia.tempMin}°",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun compartirPronostico(context: android.content.Context, ciudad: Ciudad?, clima: ClimaActual?) {
    if (ciudad == null || clima == null) return

    val condicion = clima.estado.replaceFirstChar { it.uppercase() }

    // Texto a compartir, usando las propiedades correctas de ClimaActual
    val textoParaCompartir = """
        Pronóstico del tiempo para ${ciudad.name}:
        - Temperatura: ${clima.temperatura}°C
        - Condición: $condicion
        - Humedad: ${clima.humedad}%
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textoParaCompartir)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Compartir pronóstico vía...")
    context.startActivity(shareIntent)
}
