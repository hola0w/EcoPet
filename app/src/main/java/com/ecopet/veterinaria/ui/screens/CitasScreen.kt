package com.ecopet.veterinaria.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.model.Cita
import com.ecopet.veterinaria.ui.components.EcoBackground
import com.ecopet.veterinaria.ui.components.EcoLogo
import com.ecopet.veterinaria.ui.components.EcoOutlinedButton
import com.ecopet.veterinaria.ui.components.EcoPrimaryButton
import com.ecopet.veterinaria.ui.components.formatPhone
import com.ecopet.veterinaria.ui.theme.EcoError
import com.ecopet.veterinaria.ui.theme.EcoForest
import com.ecopet.veterinaria.ui.theme.EcoMint
import com.ecopet.veterinaria.ui.theme.EcoMuted
import com.ecopet.veterinaria.ui.theme.EcoPrimary

/**
 * "Mis citas": lista de las citas guardadas en SQLite.
 * Desde cada tarjeta se puede Editar (UPDATE) o Eliminar (DELETE, con confirmación).
 */
@Composable
fun CitasScreen(
    citas: List<Cita>,
    onEdit: (Cita) -> Unit,
    onDelete: (Cita) -> Unit,
    onNew: () -> Unit,
    onHome: () -> Unit
) {
    // Cita que el usuario quiere eliminar (null = ningún diálogo abierto).
    var porEliminar by remember { mutableStateOf<Cita?>(null) }

    EcoBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 12.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EcoLogo(Modifier.size(72.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.list_title),
                style = MaterialTheme.typography.headlineMedium,
                color = EcoForest
            )
            Text(
                text = if (citas.size == 1) {
                    stringResource(R.string.list_count_one)
                } else {
                    stringResource(R.string.list_count_many, citas.size)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = EcoMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            if (citas.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(citas, key = { it.id }) { cita ->
                        CitaCard(
                            cita = cita,
                            onEdit = { onEdit(cita) },
                            onDelete = { porEliminar = cita }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Botones: mismo tamaño, mitad y mitad (igual que el formulario).
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EcoOutlinedButton(
                    text = stringResource(R.string.btn_home_short),
                    onClick = onHome,
                    modifier = Modifier.weight(1f)
                )
                EcoPrimaryButton(
                    text = stringResource(R.string.btn_new),
                    onClick = onNew,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Confirmación antes de borrar.
    porEliminar?.let { cita ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text(stringResource(R.string.dialog_delete_title), color = EcoForest) },
            text = {
                Text(
                    stringResource(
                        R.string.dialog_delete_msg,
                        cita.propietario,
                        cita.fecha,
                        cita.hora
                    ),
                    color = EcoMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(cita)
                    porEliminar = null
                }) { Text(stringResource(R.string.btn_delete), color = EcoError) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) {
                    Text(stringResource(R.string.btn_cancel), color = EcoMuted)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun CitaCard(
    cita: Cita,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(start = 14.dp, end = 8.dp, top = 14.dp, bottom = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(EcoMint, CircleShape),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text(stringResource(R.string.btn_edit), color = EcoPrimary)
                }
                TextButton(onClick = onDelete) {
                    Text(stringResource(R.string.btn_delete), color = EcoError)
                }
            }
        }
    }
}
