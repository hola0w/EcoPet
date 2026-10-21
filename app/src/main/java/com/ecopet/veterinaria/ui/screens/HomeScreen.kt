package com.ecopet.veterinaria.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.ui.components.EcoBackground
import com.ecopet.veterinaria.ui.components.EcoLogo
import com.ecopet.veterinaria.ui.components.EcoOutlinedButton
import com.ecopet.veterinaria.ui.components.EcoPrimaryButton
import com.ecopet.veterinaria.ui.theme.EcoForest
import com.ecopet.veterinaria.ui.theme.EcoMuted

@Composable
fun HomeScreen(
    onBook: () -> Unit,
    onServices: () -> Unit,
    onExit: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    EcoBackground(showMascots = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()) // con 4 botones, evita cortes en pantallas pequeñas
                .padding(horizontal = 28.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + scaleIn(
                    initialScale = 0.6f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                )
            ) {
                EcoLogo(Modifier.size(128.dp))
            }

            Spacer(Modifier.height(14.dp))

            Reveal(visible, 150) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                    color = EcoForest
                )
            }
            Reveal(visible, 220) {
                Text(
                    text = stringResource(R.string.home_badge).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = EcoForest,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }
            Reveal(visible, 290) {
                Text(
                    text = stringResource(R.string.home_tagline),
                    style = MaterialTheme.typography.bodyLarge,
                    color = EcoMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(Modifier.height(30.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Reveal(visible, 380) {
                    EcoPrimaryButton(stringResource(R.string.btn_book), onBook, Modifier.fillMaxWidth())
                }
                Reveal(visible, 440) {
                    EcoPrimaryButton(stringResource(R.string.btn_services), onServices, Modifier.fillMaxWidth())
                }
                Reveal(visible, 500) {
                    EcoOutlinedButton(stringResource(R.string.btn_exit), onExit, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

/** Aparece con desvanecimiento y un pequeño deslizamiento, con retraso escalonado. */
@Composable
private fun Reveal(visible: Boolean, delayMillis: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500, delayMillis)) + slideInVertically(tween(500, delayMillis)) { it / 2 }
    ) {
        content()
    }
}