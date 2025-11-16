package com.istea.appdelclima.presentation.ciudades

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.istea.appdelclima.repository.modelos.Ciudad
import com.istea.appdelclima.ui.theme.azulFondo
import com.istea.appdelclima.ui.theme.azulFondoComponentes
import com.istea.appdelclima.ui.theme.grisFondo

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
            BarraBusqueda(
                query = s.query,
                onQueryChange = vm::onQueryChange,
                onGeoClick = { vm.onClickGeo(onSeleccionar) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (s.cargando) {
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

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
                    val displayName = c.name

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
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = countryCodeToFlagEmoji(c.country),
                                fontSize = 32.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            Column {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = getCountryNameEs(c.country),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        s.error?.let {
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

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

@Composable
fun ProvideContextToVM(vm: CiudadesViewModel) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        vm.currentContext = ctx
    }
}

fun countryCodeToFlagEmoji(code: String?): String {
    if (code.isNullOrBlank()) return "🏳️"
    return code.uppercase()
        .map { char -> Character.codePointAt(char.toString(), 0) - 0x41 + 0x1F1E6 }
        .joinToString("") { codePoint -> String(Character.toChars(codePoint)) }
}

fun getCountryNameEs(code: String?): String {
    return when (code?.uppercase()) {
        "AR" -> "Argentina"
        "UY" -> "Uruguay"
        "CL" -> "Chile"
        "BR" -> "Brasil"
        "PY" -> "Paraguay"
        "BO" -> "Bolivia"
        "PE" -> "Perú"
        "MX" -> "México"
        "US" -> "Estados Unidos"
        "ES" -> "España"
        "NL" -> "Países Bajos"
        "IT" -> "Italia"
        "FR" -> "Francia"
        "DE" -> "Alemania"
        else -> code ?: "Sin país"
    }
}