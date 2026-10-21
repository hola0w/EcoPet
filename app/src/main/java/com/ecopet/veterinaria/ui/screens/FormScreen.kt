@file:OptIn(ExperimentalMaterial3Api::class)

package com.ecopet.veterinaria.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.model.Cita
import com.ecopet.veterinaria.ui.components.EcoBackground
import com.ecopet.veterinaria.ui.components.EcoLogo
import com.ecopet.veterinaria.ui.components.EcoOutlinedButton
import com.ecopet.veterinaria.ui.components.EcoPrimaryButton
import com.ecopet.veterinaria.ui.components.PhoneVisualTransformation
import com.ecopet.veterinaria.ui.components.formatPhone
import com.ecopet.veterinaria.ui.theme.EcoError
import com.ecopet.veterinaria.ui.theme.EcoForest
import com.ecopet.veterinaria.ui.theme.EcoMint
import com.ecopet.veterinaria.ui.theme.EcoMuted
import com.ecopet.veterinaria.ui.theme.EcoOutline
import com.ecopet.veterinaria.ui.theme.EcoPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val FieldShape = RoundedCornerShape(16.dp)

/** Posición del encabezado "Citas registradas" dentro de la LazyColumn (0 = título, 1 = campos, 2 = botones). */
private const val LIST_HEADER_INDEX = 3

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

/**
 * Pantalla única del CRUD de citas (SQLite):
 *  1. Formulario arriba.
 *  2. Botones Guardar · Consultar · Actualizar · Eliminar.
 *  3. Lista de las citas guardadas DEBAJO del formulario, en esta misma pantalla.
 *
 * Al tocar una cita de la lista, sus datos se cargan en el formulario; así
 * Actualizar y Eliminar actúan sobre esa cita (por su id).
 */
@Composable
fun FormScreen(
    citas: List<Cita>,
    onGuardar: (Cita) -> Boolean,
    onConsultar: () -> Unit,
    onActualizar: (Cita) -> Boolean,
    onEliminar: (Long) -> Boolean,
    onHome: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Estado del formulario (sobrevive a rotaciones de pantalla).
    var owner by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") } // solo dígitos
    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }
    var breed by rememberSaveable { mutableStateOf("") }
    var size by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    // id de la cita seleccionada en la lista (null = formulario en modo "nueva cita").
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selected = citas.firstOrNull { it.id == selectedId }
    val editing = selected != null

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Mensajes (Toast).
    val msgSaved = stringResource(R.string.toast_saved)
    val msgUpdated = stringResource(R.string.toast_updated)
    val msgDeleted = stringResource(R.string.toast_deleted)
    val msgQueried = stringResource(R.string.toast_queried)

    // Validación: los errores se muestran después de pulsar Guardar o Actualizar.
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

    fun clearForm() {
        owner = ""
        phone = ""
        date = ""
        time = ""
        breed = ""
        size = ""
        submitted = false
        selectedId = null
    }

    fun isValid(): Boolean =
        owner.trim().length >= 3 && phone.length == 10 &&
                date.isNotEmpty() && time.isNotEmpty() &&
                breed.trim().isNotEmpty() && size.isNotEmpty()

    fun buildCita(id: Long) = Cita(
        propietario = owner.trim(),
        telefono = phone,
        fecha = date,
        hora = time,
        raza = breed.trim(),
        tamano = size,
        id = id
    )

    // Tocar una cita de la lista la carga en el formulario (o la deselecciona si ya estaba elegida).
    fun selectCita(cita: Cita) {
        focusManager.clearFocus()
        if (cita.id == selectedId) {
            clearForm()
            return
        }
        owner = cita.propietario
        phone = cita.telefono
        date = cita.fecha
        time = cita.hora
        breed = cita.raza
        size = cita.tamano
        submitted = false
        selectedId = cita.id
        scope.launch { listState.animateScrollToItem(0) } // subir al formulario para revisarla
    }

    EcoBackground {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp)
        ) {
            // ───── Encabezado ─────
            item {
                Column {
                    TextButton(onClick = onHome, contentPadding = PaddingValues(horizontal = 4.dp)) {
                        Text(stringResource(R.string.btn_back_home), color = EcoPrimary)
                    }
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
                }
            }

            // ───── Campos del formulario ─────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
            }

            // ───── Botones del CRUD ─────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // GUARDAR (INSERT): solo cuando no hay una cita seleccionada.
                        EcoPrimaryButton(
                            text = stringResource(R.string.btn_save),
                            enabled = !editing,
                            onClick = {
                                submitted = true
                                if (isValid() && onGuardar(buildCita(0L))) {
                                    Toast.makeText(context, msgSaved, Toast.LENGTH_SHORT).show()
                                    focusManager.clearFocus()
                                    clearForm()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        // CONSULTAR (SELECT): vuelve a leer la base y muestra la lista.
                        EcoOutlinedButton(
                            text = stringResource(R.string.btn_query),
                            onClick = {
                                focusManager.clearFocus()
                                onConsultar()
                                Toast.makeText(context, msgQueried, Toast.LENGTH_SHORT).show()
                                scope.launch { listState.animateScrollToItem(LIST_HEADER_INDEX) }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // ACTUALIZAR (UPDATE ... WHERE id): solo con una cita seleccionada.
                        EcoPrimaryButton(
                            text = stringResource(R.string.btn_update),
                            enabled = editing,
                            onClick = {
                                submitted = true
                                val target = selected
                                if (target != null && isValid() && onActualizar(buildCita(target.id))) {
                                    Toast.makeText(context, msgUpdated, Toast.LENGTH_SHORT).show()
                                    focusManager.clearFocus()
                                    clearForm()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        // ELIMINAR (DELETE ... WHERE id): pide confirmación.
                        EcoOutlinedButton(
                            text = stringResource(R.string.btn_delete),
                            enabled = editing,
                            destructive = true,
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            clearForm()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(stringResource(R.string.btn_clear), color = EcoMuted)
                    }
                }
            }

            // ───── Lista de citas (debajo del formulario) ─────
            item {
                Column(Modifier.padding(top = 8.dp, bottom = 10.dp)) {
                    Text(
                        text = stringResource(R.string.list_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = EcoForest
                    )
                    Text(
                        text = if (citas.size == 1) {
                            stringResource(R.string.list_count_one)
                        } else {
                            stringResource(R.string.list_count_many, citas.size)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = EcoMuted
                    )
                    if (citas.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.list_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = EcoMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            if (citas.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🐾", fontSize = 48.sp)
                        Text(
                            text = stringResource(R.string.list_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = EcoForest,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.list_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = EcoMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                items(citas, key = { it.id }) { cita ->
                    CitaCard(
                        cita = cita,
                        selected = cita.id == selectedId,
                        onClick = { selectCita(cita) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }

    // Confirmación antes de borrar la cita seleccionada.
    val toDelete = selected
    if (showDeleteDialog && toDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_title), color = EcoForest) },
            text = {
                Text(
                    stringResource(
                        R.string.dialog_delete_msg,
                        toDelete.propietario,
                        toDelete.fecha,
                        toDelete.hora
                    ),
                    color = EcoMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    if (onEliminar(toDelete.id)) {
                        Toast.makeText(context, msgDeleted, Toast.LENGTH_SHORT).show()
                    }
                    clearForm()
                }) { Text(stringResource(R.string.btn_delete), color = EcoError) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel), color = EcoMuted)
                }
            },
            containerColor = Color.White
        )
    } else if (showDeleteDialog) {
        showDeleteDialog = false
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

/** Tarjeta de una cita guardada. Al tocarla se carga en el formulario; la elegida se resalta. */
@Composable
private fun CitaCard(
    cita: Cita,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) EcoMint else Color.White.copy(alpha = 0.92f)
        ),
        border = if (selected) BorderStroke(2.dp, EcoPrimary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (selected) Color.White else EcoMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🐾", fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = cita.propietario,
                        style = MaterialTheme.typography.titleMedium,
                        color = EcoForest
                    )
                    Text(
                        text = "📞 " + formatPhone(cita.telefono),
                        style = MaterialTheme.typography.bodyMedium,
                        color = EcoMuted
                    )
                }
                if (selected) {
                    Text(
                        text = stringResource(R.string.card_selected),
                        style = MaterialTheme.typography.labelMedium,
                        color = EcoPrimary
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "📅 ${cita.fecha}   🕒 ${cita.hora}",
                style = MaterialTheme.typography.bodyMedium,
                color = EcoForest
            )
            Text(
                text = "🐶 ${cita.raza} · ${cita.tamano}",
                style = MaterialTheme.typography.bodyMedium,
                color = EcoForest,
                modifier = Modifier.padding(top = 2.dp)
            )
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