package com.ecopet.veterinaria.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ecopet.veterinaria.model.AppointmentViewModel
import com.ecopet.veterinaria.ui.screens.FormScreen
import com.ecopet.veterinaria.ui.screens.HomeScreen
import com.ecopet.veterinaria.ui.screens.ServicesScreen

object Routes {
    const val HOME = "inicio"

    /** Pantalla única del CRUD: formulario + botones + lista de citas debajo. */
    const val FORM = "agendar_cita"
    const val SERVICES = "servicios"
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


@Composable
fun EcoPetApp(viewModel: AppointmentViewModel = viewModel()) {
    val nav = rememberNavController()
    val context = LocalContext.current

    fun goHome() {
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

        // CRUD completo en UNA sola pantalla: Guardar, Consultar, Actualizar y Eliminar.
        composable(Routes.FORM) {
            FormScreen(
                citas = viewModel.citas,
                onGuardar = { viewModel.guardar(it) },
                onConsultar = { viewModel.cargar() },
                onActualizar = { viewModel.actualizar(it) },
                onEliminar = { viewModel.eliminar(it) },
                onHome = { goHome() }
            )
        }

        composable(Routes.SERVICES) {
            ServicesScreen(onHome = { goHome() })
        }
    }
}