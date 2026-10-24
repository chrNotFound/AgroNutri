package cl.duoc.agronutri.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.agronutri.AgroApp

@Composable
private fun detalleVm(app: AgroApp, id: String): DetallePlanViewModel {
    return viewModel(factory = DetallePlanViewModelFactory(app.repo, id))
}

@Composable
fun AgroNavHost() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as AgroApp
    val perfil by app.prefs.perfil.collectAsState()
    val tema by app.prefs.tema.collectAsState()

    val inicio = remember { if (app.prefs.perfil.value.isBlank()) "bienvenida" else "planes" }

    NavHost(navController = nav, startDestination = inicio) {

        composable("bienvenida") {
            BienvenidaScreen(onElegir = { perfilElegido ->
                app.prefs.cambiarPerfil(perfilElegido)
                nav.navigate("planes") { popUpTo("bienvenida") { inclusive = true } }
            })
        }

        composable("planes") {
            val vm: PlanesViewModel = viewModel(factory = PlanesViewModelFactory(app.repo, app.conectividad))
            PlanesScreen(
                vm = vm,
                onNuevo = { nav.irA("nuevo") },
                onAbrir = { id -> nav.irA("plan/$id") },
                onCatalogo = { nav.irA("catalogo") },
                onAjustes = { nav.irA("ajustes") }
            )
        }

        composable("nuevo") {
            val vm: PlanesViewModel = viewModel(factory = PlanesViewModelFactory(app.repo, app.conectividad))
            NuevoPlanScreen(vm) { nav.volver() }
        }

        composable("plan/{id}") { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            DetallePlanScreen(
                vm = detalleVm(app, id),
                perfil = perfil,
                onAtras = { nav.volver() },
                onAnalisis = { nav.irA("analisis/$id") },
                onFotos = { nav.irA("fotos/$id") },
                onPrograma = { nav.irA("programa/$id") },
                onHistorial = { nav.irA("historial/$id") }
            )
        }

        composable("analisis/{id}") { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            AnalisisScreen(detalleVm(app, id)) { nav.volver() }
        }

        composable("programa/{id}") { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            ProgramaScreen(detalleVm(app, id), perfil) { nav.volver() }
        }

        composable("historial/{id}") { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            HistorialScreen(detalleVm(app, id), perfil) { nav.volver() }
        }

        composable("fotos/{id}") { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            FotosScreen(detalleVm(app, id)) { nav.volver() }
        }

        composable("catalogo") {
            CatalogoScreen { nav.volver() }
        }

        composable("ajustes") {
            AjustesScreen(
                tema = tema,
                perfil = perfil,
                onTema = { app.prefs.cambiarTema(it) },
                onPerfil = { app.prefs.cambiarPerfil(it) },
                onAtras = { nav.volver() }
            )
        }
    }
}

private fun NavController.puedeNavegar(): Boolean {
    val actual = currentBackStackEntry
    if (actual == null) {
        return false
    }
    return actual.lifecycle.currentState == Lifecycle.State.RESUMED
}

private fun NavController.irA(ruta: String) {
    if (puedeNavegar()) {
        navigate(ruta)
    }
}

private fun NavController.volver() {
    if (puedeNavegar() && previousBackStackEntry != null) {
        popBackStack()
    }
}
