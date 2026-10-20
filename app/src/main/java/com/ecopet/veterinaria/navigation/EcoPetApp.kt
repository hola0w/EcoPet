package com.ecopet.veterinaria.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ecopet.veterinaria.model.AppointmentViewModel
import com.ecopet.veterinaria.ui.screens.FormScreen
import com.ecopet.veterinaria.ui.screens.HomeScreen
import com.ecopet.veterinaria.ui.screens.ServicesScreen
import com.ecopet.veterinaria.ui.screens.SummaryScreen

object Routes {
    const val HOME = "inicio"
    const val FORM = "agendar_cita"
    const val SUMMARY = "resumen_cita"
    const val SERVICES = "servicios"
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Grafo de navegación de la app:
 *  Inicio → Agendar cita → Formulario → Guardar → Resumen → Inicio
 *  Inicio → Nuestros servicios → Servicios → Inicio
 *  Inicio → Salir → Cierra la aplicación
 */
@Composable
fun EcoPetApp(viewModel: AppointmentViewModel = viewModel()) {
    val nav = rememberNavController()
    val context = LocalContext.current

    fun goHome() {
        viewModel.limpiar()
        nav.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = nav,
        startDestination = Routes.HOME,
        enterTransition = { fadeIn(tween(320)) + slideInHorizontally(tween(320)) { it / 8 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(320)) },
        popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(260)) { it / 8 } }
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onBook = { nav.navigate(Routes.FORM) },
                onServices = { nav.navigate(Routes.SERVICES) },
                onExit = { context.findActivity()?.finishAffinity() }
            )
        }

        composable(Routes.FORM) {
            FormScreen(
                onCancel = { goHome() },
                onSave = { cita ->
                    viewModel.guardar(cita)
                    nav.navigate(Routes.SUMMARY) {
                        // El formulario no queda en la pila: "atrás" desde el resumen vuelve al inicio.
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.SUMMARY) {
            val cita = viewModel.cita
            BackHandler { goHome() }
            if (cita == null) {
                // Seguridad: si no hay datos (p. ej. el proceso se reinició) se vuelve al inicio.
                LaunchedEffect(Unit) { goHome() }
            } else {
                SummaryScreen(cita = cita, onHome = { goHome() })
            }
        }

        composable(Routes.SERVICES) {
            ServicesScreen(onHome = { goHome() })
        }
    }
}
