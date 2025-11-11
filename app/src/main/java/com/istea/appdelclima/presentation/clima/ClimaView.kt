package com.istea.appdelclima.presentation.clima

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ClimaView(vm: ClimaViewModel, onCambiarCiudad: () -> Unit) {
    val s by vm.estado
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(s.ciudad?.name ?: "", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onCambiarCiudad) { Text("Cambiar ciudad") }
        }
        if (s.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
        s.hoy?.let { Text("Ahora: ${it.temperatura}°C • ${it.estado} • Hum ${it.humedad}%\n", Modifier.padding(top = 8.dp)) }
        s.dias.forEach { d -> Text("• ${d.dia}: ${d.tempMin}°/${d.tempMax}°  ${d.estado}") }
        s.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
    }
}
