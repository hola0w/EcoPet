package com.ecopet.veterinaria.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.ui.theme.EcoForest
import com.ecopet.veterinaria.ui.theme.EcoMint
import com.ecopet.veterinaria.ui.theme.EcoPrimary
import com.ecopet.veterinaria.ui.theme.EcoSky

/** Altura y forma únicas para TODOS los botones de la app (tamaños consistentes). */
val EcoButtonHeight = 56.dp
val EcoButtonShape = RoundedCornerShape(18.dp)

/**
 * Fondo de EcoPet: degradado verde suave + patrón de huellas, hojas y huesitos.
 * En la pantalla de inicio además asoman un perro y un gato en la parte inferior.
 */
@Composable
fun EcoBackground(
    modifier: Modifier = Modifier,
    showMascots: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = if (showMascots) listOf(Color(0xFFE9F6EE), Color(0xFFC6E3D0)) else listOf(EcoSky, EcoMint)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors))
    ) {
        Image(
            painter = painterResource(R.drawable.bg_pattern),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (showMascots) {
            Image(
                painter = painterResource(R.drawable.bg_mascots),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
        content()
    }
}

@Composable
fun EcoLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_ecopet_logo),
        contentDescription = stringResource(R.string.logo_description),
        modifier = modifier.shadow(10.dp, CircleShape, clip = false, spotColor = EcoPrimary, ambientColor = EcoPrimary)
    )
}

@Composable
fun EcoPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(EcoButtonHeight),
        shape = EcoButtonShape,
        colors = ButtonDefaults.buttonColors(containerColor = EcoPrimary, contentColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun EcoOutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(EcoButtonHeight),
        shape = EcoButtonShape,
        border = BorderStroke(1.5.dp, EcoPrimary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White.copy(alpha = 0.7f),
            contentColor = EcoForest
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
