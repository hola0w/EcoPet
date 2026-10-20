package com.ecopet.veterinaria.ui.screens

import androidx.annotation.StringRes
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.ui.components.EcoBackground
import com.ecopet.veterinaria.ui.components.EcoLogo
import com.ecopet.veterinaria.ui.components.EcoPrimaryButton
import com.ecopet.veterinaria.ui.theme.EcoForest
import com.ecopet.veterinaria.ui.theme.EcoMint
import com.ecopet.veterinaria.ui.theme.EcoMuted

private data class Service(
    val icon: String,
    @StringRes val title: Int,
    @StringRes val description: Int
)

private val services = listOf(
    Service("🩺", R.string.service_consulta, R.string.service_consulta_desc),
    Service("💉", R.string.service_vacunacion, R.string.service_vacunacion_desc),
    Service("🦠", R.string.service_desparasitacion, R.string.service_desparasitacion_desc),
    Service("🛁", R.string.service_bano, R.string.service_bano_desc),
    Service("🚑", R.string.service_emergencias, R.string.service_emergencias_desc),
    Service("🏥", R.string.service_cirugia, R.string.service_cirugia_desc),
    Service("📋", R.string.service_control, R.string.service_control_desc)
)

@Composable
fun ServicesScreen(onHome: () -> Unit) {
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
                text = stringResource(R.string.services_title),
                style = MaterialTheme.typography.headlineMedium,
                color = EcoForest
            )
            Text(
                text = stringResource(R.string.services_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = EcoMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(services) { ServiceCard(it) }
            }

            Spacer(Modifier.height(14.dp))
            EcoPrimaryButton(stringResource(R.string.btn_home), onHome, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ServiceCard(service: Service) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(EcoMint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(service.icon, fontSize = 26.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = stringResource(service.title),
                    style = MaterialTheme.typography.titleMedium,
                    color = EcoForest
                )
                Text(
                    text = stringResource(service.description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = EcoMuted
                )
            }
        }
    }
}
