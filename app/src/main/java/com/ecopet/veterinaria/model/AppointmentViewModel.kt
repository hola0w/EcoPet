package com.ecopet.veterinaria.model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.ecopet.veterinaria.data.CitaDbHelper

/**
 * Puente entre la pantalla y SQLite.
 * Cada operación del CRUD escribe en la base y luego vuelve a leerla, así la lista
 * que se muestra debajo del formulario siempre refleja lo que hay guardado.
 */
class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CitaDbHelper(application)

    /** Citas guardadas en SQLite (la más reciente primero). */
    var citas by mutableStateOf<List<Cita>>(emptyList())
        private set

    init {
        cargar()
    }

    /** CONSULTAR: vuelve a leer todas las citas desde la base de datos. */
    fun cargar() {
        citas = db.obtenerTodas()
    }

    /** GUARDAR: inserta una cita nueva. Devuelve true si se guardó. */
    fun guardar(nueva: Cita): Boolean {
        val id = db.insertar(nueva)
        cargar()
        return id != -1L
    }

    /** ACTUALIZAR: modifica la cita que tiene ese mismo id. Devuelve true si cambió una fila. */
    fun actualizar(editada: Cita): Boolean {
        val filas = db.actualizar(editada)
        cargar()
        return filas > 0
    }

    /** ELIMINAR: borra la cita con ese id. Devuelve true si se borró una fila. */
    fun eliminar(id: Long): Boolean {
        val filas = db.eliminar(id)
        cargar()
        return filas > 0
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}