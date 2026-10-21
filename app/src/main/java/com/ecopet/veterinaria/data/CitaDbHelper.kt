package com.ecopet.veterinaria.data


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ecopet.veterinaria.model.Cita


class CitaDbHelper(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "ecopet.db"
        const val DB_VERSION = 1

        const val TABLA = "citas"
        const val COL_ID = "id"
        const val COL_PROPIETARIO = "propietario"
        const val COL_TELEFONO = "telefono"
        const val COL_FECHA = "fecha"
        const val COL_HORA = "hora"
        const val COL_RAZA = "raza"
        const val COL_TAMANO = "tamano"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PROPIETARIO TEXT NOT NULL,
                $COL_TELEFONO TEXT NOT NULL,
                $COL_FECHA TEXT NOT NULL,
                $COL_HORA TEXT NOT NULL,
                $COL_RAZA TEXT NOT NULL,
                $COL_TAMANO TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Versión 1: no hay migraciones todavía. Si cambia el esquema, aquí se migra.
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }

    // ───────────── CREATE ─────────────

    fun insertar(cita: Cita): Long =
        writableDatabase.insert(TABLA, null, cita.toValues())

    // ───────────── READ ─────────────

    fun obtenerTodas(): List<Cita> {
        val lista = mutableListOf<Cita>()
        readableDatabase.query(
            TABLA, null, null, null, null, null, "$COL_ID DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(cursor.toCita())
        }
        return lista
    }

    fun obtenerPorId(id: Long): Cita? =
        readableDatabase.query(
            TABLA, null, "$COL_ID = ?", arrayOf(id.toString()), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toCita() else null
        }

    // ───────────── UPDATE ─────────────

    fun actualizar(cita: Cita): Int =
        writableDatabase.update(
            TABLA, cita.toValues(), "$COL_ID = ?", arrayOf(cita.id.toString())
        )

    // ───────────── DELETE ─────────────

    fun eliminar(id: Long): Int =
        writableDatabase.delete(TABLA, "$COL_ID = ?", arrayOf(id.toString()))

    // ───────────── Conversión Cita ⇄ fila ─────────────

    private fun Cita.toValues() = ContentValues().apply {
        put(COL_PROPIETARIO, propietario)
        put(COL_TELEFONO, telefono)
        put(COL_FECHA, fecha)
        put(COL_HORA, hora)
        put(COL_RAZA, raza)
        put(COL_TAMANO, tamano)
    }

    private fun Cursor.toCita() = Cita(
        id = getLong(getColumnIndexOrThrow(COL_ID)),
        propietario = getString(getColumnIndexOrThrow(COL_PROPIETARIO)),
        telefono = getString(getColumnIndexOrThrow(COL_TELEFONO)),
        fecha = getString(getColumnIndexOrThrow(COL_FECHA)),
        hora = getString(getColumnIndexOrThrow(COL_HORA)),
        raza = getString(getColumnIndexOrThrow(COL_RAZA)),
        tamano = getString(getColumnIndexOrThrow(COL_TAMANO))
    )
}
