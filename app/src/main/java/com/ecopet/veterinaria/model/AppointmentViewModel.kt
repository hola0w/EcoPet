package com.ecopet.veterinaria.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * Guarda la última cita registrada para que la pantalla "Resumen de la cita"
 * pueda mostrar exactamente los datos introducidos en el formulario.
 */
class AppointmentViewModel : ViewModel() {
    var cita by mutableStateOf<Cita?>(null)
        private set

    fun guardar(nueva: Cita) {
        cita = nueva
    }

    fun limpiar() {
        cita = null
    }
}
