@file:OptIn(ExperimentalMaterial3Api::class)

package com.ecopet.veterinaria.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.model.Cita
import com.ecopet.veterinaria.ui.components.EcoBackground
import com.ecopet.veterinaria.ui.components.EcoLogo
import com.ecopet.veterinaria.ui.components.EcoOutlinedButton
import com.ecopet.veterinaria.ui.components.EcoPrimaryButton
import com.ecopet.veterinaria.ui.components.PhoneVisualTransformation
import com.ecopet.veterinaria.ui.theme.EcoError
import com.ecopet.veterinaria.ui.theme.EcoForest
import com.ecopet.veterinaria.ui.theme.EcoMuted
import com.ecopet.veterinaria.ui.theme.EcoOutline
import com.ecopet.veterinaria.ui.theme.EcoPrimary
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val FieldShape = RoundedCornerShape(16.dp)

/** Solo permite elegir el día de hoy o fechas futuras. */
private object FutureDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val today = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        return utcTimeMillis >= today
    }

    override fun isSelectableYear(year: Int): Boolean = year >= LocalDate.now().year
}

private fun formatDate(utcMillis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(utcMillis))

private fun formatTime(hour24: Int, minute: Int): String {
    val hour12 = if (hour24 % 12 == 0) 12 else hour24 % 12
    val suffix = if (hour24 < 12) "AM" else "PM"
    return String.format(Locale.US, "%02d:%02d %s", hour12, minute, suffix)
}

@Composable
fun FormScreen(
    onCancel: () -> Unit,
    onSave: (Cita) -> Unit,
    /** Si no es null, el formulario está en modo EDITAR y arranca con los datos de esa cita. */
    initial: Cita? = null
) {
    val editing = initial != null

    // Estado del formulario (sobrevive a rotaciones de pantalla).
    var owner by rememberSaveable { mutableStateOf(initial?.propietario ?: "") }
    var phone by rememberSaveable { mutableStateOf(initial?.telefono ?: "") } // solo dígitos
    var date by rememberSaveable { mutableStateOf(initial?.fecha ?: "") }
    var time by rememberSaveable { mutableStateOf(initial?.hora ?: "") }
    var breed by rememberSaveable { mutableStateOf(initial?.raza ?: "") }
    var size by rememberSaveable { mutableStateOf(initial?.tamano ?: "") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Validación: los errores se muestran después de pulsar "Guardar".
    val ownerError = if (submitted && owner.trim().length < 3) stringResource(R.string.err_owner) else null
    val phoneError = if (submitted && phone.length != 10) stringResource(R.string.err_phone) else null
    val dateError = if (submitted && date.isEmpty()) stringResource(R.string.err_date) else null
    val timeError = if (submitted && time.isEmpty()) stringResource(R.string.err_time) else null
    val breedError = if (submitted && breed.trim().isEmpty()) stringResource(R.string.err_breed) else null
    val sizeError = if (submitted && size.isEmpty()) stringResource(R.string.err_size) else null

    val sizes = listOf(
        stringResource(R.string.size_small),
        stringResource(R.string.size_medium),
        stringResource(R.string.size_large)
    )

    EcoBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 22.dp)
                .padding(top = 12.dp, bottom = 16.dp)
        ) {
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(if (editing) R.string.form_title_edit else R.string.form_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = EcoForest
                    )
                    Text(
                        text = stringResource(if (editing) R.string.form_subtitle_edit else R.string.form_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = EcoMuted
                    )
                }
                EcoLogo(Modifier.size(56.dp))
            }

            Spacer(Modifier.height(14.dp))

            // Campos
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EcoField(
                    value = owner,
                    onValueChange = { owner = it },
                    label = stringResource(R.string.label_owner),
                    error = ownerError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )
                EcoField(
                    value = phone,
                    onValueChange = { phone = it.filter(Char::isDigit).take(10) },
                    label = stringResource(R.string.label_phone),
                    error = phoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = PhoneVisualTransformation
                )
                PickerField(
                    value = date,
                    label = stringResource(R.string.label_date),
                    error = dateError,
                    onClick = { showDatePicker = true }
                )
                PickerField(
                    value = time,
                    label = stringResource(R.string.label_time),
                    error = timeError,
                    onClick = { showTimePicker = true }
                )
                EcoField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = stringResource(R.string.label_breed),
                    error = breedError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    )
                )

                // Tamaño de la mascota: opciones rápidas de un toque.
                Text(
                    text = stringResource(R.string.label_size),
                    style = MaterialTheme.typography.labelMedium,
                    color = EcoMuted,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    sizes.forEach { option ->
                        SizeOption(
                            text = option,
                            selected = size == option,
                            onClick = { size = option },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (sizeError != null) {
                    Text(
                        text = sizeError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = EcoError,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Botones: mismo tamaño, mitad y mitad.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EcoOutlinedButton(
                    text = stringResource(R.string.btn_cancel),
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
                EcoPrimaryButton(
                    text = stringResource(if (editing) R.string.btn_update else R.string.btn_save),
                    onClick = {
                        submitted = true
                        val valid = owner.trim().length >= 3 && phone.length == 10 &&
                                date.isNotEmpty() && time.isNotEmpty() &&
                                breed.trim().isNotEmpty() && size.isNotEmpty()
                        if (valid) {
                            onSave(
                                Cita(
                                    propietario = owner.trim(),
                                    telefono = phone,
                                    fecha = date,
                                    hora = time,
                                    raza = breed.trim(),
                                    tamano = size,
                                    id = initial?.id ?: 0L // se conserva el id al editar
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Selector de fecha
    if (showDatePicker) {
        val state = rememberDatePickerState(selectableDates = FutureDates)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { date = formatDate(it) }
                    showDatePicker = false
                }) { Text(stringResource(R.string.dialog_ok), color = EcoPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.btn_cancel), color = EcoMuted)
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    // Selector de hora
    if (showTimePicker) {
        val state = rememberTimePickerState(initialHour = 9, initialMinute = 0, is24Hour = false)
        Dialog(
            onDismissRequest = { showTimePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 360.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.dialog_time_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = EcoMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    TimePicker(state = state)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(stringResource(R.string.btn_cancel), color = EcoMuted)
                        }
                        TextButton(onClick = {
                            time = formatTime(state.hour, state.minute)
                            showTimePicker = false
                        }) { Text(stringResource(R.string.dialog_ok), color = EcoPrimary) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EcoField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        readOnly = readOnly,
        isError = error != null,
        supportingText = { Text(error ?: " ", color = if (error != null) EcoError else Color.Transparent) },
        trailingIcon = trailing,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        shape = FieldShape,
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EcoPrimary,
            unfocusedBorderColor = EcoOutline,
            errorBorderColor = EcoError,
            focusedLabelColor = EcoPrimary,
            unfocusedLabelColor = EcoMuted,
            errorLabelColor = EcoError,
            cursorColor = EcoPrimary,
            focusedTextColor = EcoForest,
            unfocusedTextColor = EcoForest,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
            errorContainerColor = Color.White
        )
    )
}

/** Campo de solo lectura que abre un selector (fecha u hora) al tocarlo. */
@Composable
private fun PickerField(
    value: String,
    label: String,
    error: String?,
    onClick: () -> Unit
) {
    Box {
        EcoField(
            value = value,
            onValueChange = {},
            label = label,
            error = error,
            readOnly = true,
            trailing = {
                Text(
                    text = stringResource(R.string.action_pick),
                    style = MaterialTheme.typography.labelMedium,
                    color = EcoPrimary,
                    modifier = Modifier.padding(end = 14.dp)
                )
            }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        )
    }
}

@Composable
private fun SizeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) EcoPrimary else Color.White.copy(alpha = 0.85f),
        contentColor = if (selected) Color.White else EcoForest,
        border = BorderStroke(1.dp, if (selected) EcoPrimary else EcoOutline)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
        }
    }
}
