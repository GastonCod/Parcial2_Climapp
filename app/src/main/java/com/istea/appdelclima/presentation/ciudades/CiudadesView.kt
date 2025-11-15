package com.istea.appdelclima.presentation.ciudades

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.istea.appdelclima.repository.modelos.Ciudad
import com.istea.appdelclima.ui.theme.azulFondo
import com.istea.appdelclima.ui.theme.azulFondoComponentes
import com.istea.appdelclima.ui.theme.grisFondo

// -------------------------------------------------------------------------------------------------
// MAIN VIEW
// -------------------------------------------------------------------------------------------------

@Composable
fun CiudadesView(vm: CiudadesViewModel, onSeleccionar: (Ciudad) -> Unit) {
    val s by vm.estado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(azulFondo)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(azulFondo)
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            // 🔥 NUEVA BARRA ESTÉTICA
            BarraBusqueda(
                query = s.query,
                onQueryChange = vm::onQueryChange,
                onGeoClick = { vm.buscarCiudadPorUbicacionDirecta(onSeleccionar) } // usamos VM
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (s.cargando)
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
                .weight(1f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = grisFondo),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(s.resultados) { index, c ->

                    val isFirst = index == 0
                    val isLast = index == s.resultados.lastIndex

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                            .clickable { onSeleccionar(c) },
                        shape = RoundedCornerShape(
                            topStart = if (isFirst) 16.dp else 0.dp,
                            topEnd = if (isFirst) 16.dp else 0.dp,
                            bottomStart = if (isLast) 0.dp else 0.dp,
                            bottomEnd = if (isLast) 0.dp else 0.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(c.name, style = MaterialTheme.typography.titleMedium)
                                c.country?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Text("(${c.lat}, ${c.lon})", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        s.error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

// -------------------------------------------------------------------------------------------------
// CUSTOM SEARCH BAR ESTÉTICA
// -------------------------------------------------------------------------------------------------

@Composable
fun BarraBusqueda(
    query: String,
    onQueryChange: (String) -> Unit,
    onGeoClick: () -> Unit
) {
    val fondo = azulFondoComponentes

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TEXTFIELD REDONDEADO
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(fondo, shape = RoundedCornerShape(25.dp)),
            contentAlignment = Alignment.CenterStart
        ) {

            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar ciudad…", color = Color.White.copy(alpha = 0.7f)) },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // BOTÓN DE GEO (IZQUIERDA)
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(fondo, shape = RoundedCornerShape(25.dp)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onGeoClick) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Ubicación actual",
                    tint = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// GPS BUTTON LOGIC (USADO SOLO DESDE VM)
// -------------------------------------------------------------------------------------------------

@SuppressLint("MissingPermission")
fun obtenerUbicacion(
    context: android.content.Context,
    onFound: (Double, Double) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)

    fused.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onFound(location.latitude, location.longitude)
        } else {
            println("No se pudo obtener ubicación")
        }
    }
}

// -------------------------------------------------------------------------------------------------
// VM EXTENSION: llama gps → crea ciudad → navega
// -------------------------------------------------------------------------------------------------

fun CiudadesViewModel.buscarCiudadPorUbicacionDirecta(
    onSeleccionar: (Ciudad) -> Unit
) {
    val context = currentContext ?: return

    obtenerUbicacion(context) { lat, lon ->
        val ciudadActual = Ciudad(
            name = "Ubicación Actual",
            lat = lat,
            lon = lon,
            country = "AR"
        )
        onSeleccionar(ciudadActual)
    }
}

// -------------------------------------------------------------------------------------------------
// NECESARIO PARA ACCEDER A CONTEXTO DESDE VM
// -------------------------------------------------------------------------------------------------

var CiudadesViewModel.currentContext: android.content.Context?
    get() = _ctx
    set(value) { _ctx = value }

private var _ctx: android.content.Context? = null

@Composable
fun ProvideContextToVM(vm: CiudadesViewModel) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        vm.currentContext = ctx
    }
}

// -------------------------------------------------------------------------------------------------
// PREVIEW
// -------------------------------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCiudadesView() {

    // Creamos el repo MOCK
    val mock = com.istea.appdelclima.repository.RepositorioMock()

    // Creamos el ViewModel solo para preview (sin warning)
    val vm = remember { CiudadesViewModel(mock) }

    // Cargamos datos ficticios
    vm.estado.value = CiudadesEstado(
        query = "Bue",
        resultados = listOf(
            Ciudad("Buenos Aires", -34.61, -58.38, "AR"),
            Ciudad("Burzaco", -34.82, -58.39, "AR"),
            Ciudad("Berazategui", -34.77, -58.21, "AR")
        ),
        cargando = false,
        error = null
    )

    MaterialTheme {
        CiudadesView(
            vm = vm,
            onSeleccionar = {}
        )
    }
}