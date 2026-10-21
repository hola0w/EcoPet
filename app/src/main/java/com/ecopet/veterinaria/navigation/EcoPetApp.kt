package com.ecopet.veterinaria.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ecopet.veterinaria.R
import com.ecopet.veterinaria.model.AppointmentViewModel
import com.ecopet.veterinaria.ui.screens.CitasScreen
import com.ecopet.veterinaria.ui.screens.FormScreen
import com.ecopet.veterinaria.ui.screens.HomeScreen
import com.ecopet.veterinaria.ui.screens.ServicesScreen
import com.ecopet.veterinaria.ui.screens.SummaryScreen

object Routes {
    const val HOME = "inicio"
    const val FORM = "agendar_cita"
    const val SUMMARY = "resumen_cita"
    const val SERVICES = "servicios"
    const val LIST = "mis_citas"

    const val ARG_ID = "id"
    const val EDIT = "editar_cita/{$ARG_ID}"
    fun edit(id: Long) = "editar_cita/$id"
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

    val msgUpdated = stringResource(R.string.toast_updated)
    val msgDeleted = stringResource(R.string.toast_deleted)

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
                onAppointments = { nav.navigate(Routes.LIST) },
                onServices = { nav.navigate(Routes.SERVICES) },
                onExit = { context.findActivity()?.finishAffinity() }
            )
        }

        // CREATE: formulario vacío → guarda en SQLite → resumen.
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

        // READ + DELETE: lista de citas guardadas.
        composable(Routes.LIST) {
            CitasScreen(
                citas = viewModel.citas,
                onEdit = { cita -> nav.navigate(Routes.edit(cita.id)) },
                onDelete = { cita ->
                    if (viewModel.eliminar(cita.id)) {
                        Toast.makeText(context, msgDeleted, Toast.LENGTH_SHORT).show()
                    }
                },
                onNew = { nav.navigate(Routes.FORM) },
                onHome = { goHome() }
            )
        }

        // UPDATE: mismo formulario, con los datos de la cita elegida.
        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument(Routes.ARG_ID) { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong(Routes.ARG_ID) ?: 0L
            val cita = viewModel.buscar(id)
            if (cita == null) {
                // La cita ya no existe (p. ej. fue eliminada): se regresa a la lista.
                LaunchedEffect(Unit) { nav.popBackStack() }
            } else {
                FormScreen(
                    initial = cita,
                    onCancel = { nav.popBackStack() },
                    onSave = { editada ->
                        if (viewModel.actualizar(editada)) {
                            Toast.makeText(context, msgUpdated, Toast.LENGTH_SHORT).show()
                        }
                        nav.popBackStack()
                    }
                )
            }
        }

        composable(Routes.SERVICES) {
            ServicesScreen(onHome = { goHome() })
        }
    }
}
