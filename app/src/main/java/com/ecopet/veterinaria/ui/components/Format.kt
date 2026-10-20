package com.ecopet.veterinaria.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/** 8095551234 → 809-555-1234 (solo se muestra; el valor guardado son los dígitos). */
fun formatPhone(digits: String): String = buildString {
    digits.forEachIndexed { i, c ->
        if (i == 3 || i == 6) append('-')
        append(c)
    }
}

/** Muestra el teléfono con guiones mientras el usuario escribe solo números. */
object PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 3 -> offset
                offset <= 6 -> offset + 1
                else -> offset + 2
            }

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 3 -> offset
                offset <= 7 -> offset - 1
                else -> offset - 2
            }.coerceIn(0, digits.length)
        }
        return TransformedText(AnnotatedString(formatPhone(digits)), mapping)
    }
}
