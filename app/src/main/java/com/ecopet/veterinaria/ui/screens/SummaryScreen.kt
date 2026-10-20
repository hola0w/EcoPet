package com.ecopet.veterinaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.model.Cita
import com.ecopet.veterinaria.ui.components.EcoBackground
import com.ecopet.veterinaria.ui.components.EcoLogo
import com.ecopet.veterinaria.ui.components.EcoPrimaryButton
import com.ecopet.veterinaria.ui.components.formatPhone
import com.ecopet.veterinaria.ui.theme.EcoForest
import com.ecopet.veterinaria.ui.theme.EcoMint
import com.ecopet.veterinaria.ui.theme.EcoOutline
import com.ecopet.veterinaria.ui.theme.EcoPrimary

@Composable
fun SummaryScreen(cita: Cita, onHome: () -> Unit) {
    EcoBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 12.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EcoLogo(Modifier.size(80.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.summary_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = EcoForest
                )
                Text(
                    text = "✓  " + stringResource(R.string.summary_ok),
                    style = MaterialTheme.typography.labelMedium,
                    color = EcoForest,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 16.dp)
                        .background(EcoMint, RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
                        SummaryRow(stringResource(R.string.summary_name), cita.propietario)
                        SummaryRow(stringResource(R.string.summary_phone), formatPhone(cita.telefono))
                        SummaryRow(stringResource(R.string.summary_date), cita.fecha)
                        SummaryRow(stringResource(R.string.summary_time), cita.hora)
                        SummaryRow(stringResource(R.string.summary_breed), cita.raza)
                        SummaryRow(stringResource(R.string.summary_size), cita.tamano, last = true)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            EcoPrimaryButton(stringResource(R.string.btn_home), onHome, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, last: Boolean = false) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = EcoPrimary)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = EcoForest)
    }
    if (!last) HorizontalDivider(color = EcoOutline.copy(alpha = 0.35f))
}
