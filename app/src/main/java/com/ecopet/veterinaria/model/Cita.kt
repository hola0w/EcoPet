package com.ecopet.veterinaria.model

/** Datos de una cita registrada en el formulario. El teléfono se guarda solo con dígitos. */
data class Cita(
    val propietario: String,
    val telefono: String,
    val fecha: String,
    val hora: String,
    val raza: String,
    val tamano: String
)
