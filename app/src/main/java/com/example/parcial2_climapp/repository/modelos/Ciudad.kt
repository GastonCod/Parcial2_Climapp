package com.example.parcial2_climapp.repository.modelos

data class Ciudad(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String? = null
)