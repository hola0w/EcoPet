package com.ecopet.veterinaria.model


data class Cita(
    val propietario: String,
    val telefono: String,
    val fecha: String,
    val hora: String,
    val raza: String,
    val tamano: String,
    val id: Long = 0L
)
