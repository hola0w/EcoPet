package com.ecopet.veterinaria.model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.ecopet.veterinaria.data.CitaDbHelper


class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CitaDbHelper(application)

    var citas by mutableStateOf<List<Cita>>(emptyList())
        private set

    var cita by mutableStateOf<Cita?>(null)
        private set

    init {
        cargar()
    }

    fun cargar() {
        citas = db.obtenerTodas()
    }

    fun buscar(id: Long): Cita? = citas.firstOrNull { it.id == id }

    fun guardar(nueva: Cita) {
        val id = db.insertar(nueva)
        if (id != -1L) cita = nueva.copy(id = id)
        cargar()
    }

    fun actualizar(editada: Cita): Boolean {
        val filas = db.actualizar(editada)
        cargar()
        return filas > 0
    }

    fun eliminar(id: Long): Boolean {
        val filas = db.eliminar(id)
        if (cita?.id == id) cita = null
        cargar()
        return filas > 0
    }

    fun limpiar() {
        cita = null
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
