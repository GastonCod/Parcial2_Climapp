package com.istea.appdelclima.presentation.ciudades

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.istea.appdelclima.repository.modelos.Ciudad

@Composable
fun CiudadesView(vm: CiudadesViewModel, onSeleccionar: (Ciudad) -> Unit) {
    val s by vm.estado
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = s.query,
            onValueChange = vm::onQueryChange,
            label = { Text("Buscar ciudad") },
            modifier = Modifier.fillMaxWidth()
        )
        if (s.cargando) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 8.dp)) {
            items(s.resultados) { c ->
                ListItem(
                    headlineContent = { Text("${c.name} ${c.country ?: ""}") },
                    supportingContent = { Text("(${c.lat}, ${c.lon})") },
                    modifier = Modifier.clickable { onSeleccionar(c) }
                )
                Divider()
            }
        }
        s.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
    }
}
