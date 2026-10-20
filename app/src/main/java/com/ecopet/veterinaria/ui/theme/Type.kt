package com.ecopet.veterinaria.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ecopet.veterinaria.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_medium, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

private val base = Typography()

val EcoTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Poppins),
    displayMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 40.sp, lineHeight = 46.sp),
    displaySmall = base.displaySmall.copy(fontFamily = Poppins),
    headlineLarge = base.headlineLarge.copy(fontFamily = Poppins),
    headlineMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = base.headlineSmall.copy(fontFamily = Poppins),
    titleLarge = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 24.sp),
    titleSmall = base.titleSmall.copy(fontFamily = Poppins),
    bodyLarge = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = base.bodySmall.copy(fontFamily = Poppins),
    labelLarge = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    labelMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = base.labelSmall.copy(fontFamily = Poppins)
)
