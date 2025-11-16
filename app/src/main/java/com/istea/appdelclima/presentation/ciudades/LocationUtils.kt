package com.istea.appdelclima.presentation.ciudades

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun obtenerUbicacion(
    context: Context,
    onFound: (Double, Double) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)

    fused.lastLocation.addOnSuccessListener { location ->
        println(">>> RESULTADO lastLocation = $location")

        if (location != null) {
            onFound(location.latitude, location.longitude)
        } else {
            println("No se pudo obtener ubicación (null)")
        }
    }.addOnFailureListener {
        println("Error obteniendo ubicación: ${it.message}")
    }
}