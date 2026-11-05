package cl.duoc.zaror.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.zaror.ui.screens.FinanzasScreen
import cl.duoc.zaror.ui.screens.HomeScreen
import cl.duoc.zaror.ui.screens.LoginScreen
import cl.duoc.zaror.ui.screens.ReservasScreen
import cl.duoc.zaror.viewmodel.FinanzasViewModel
import cl.duoc.zaror.viewmodel.ReservasViewModel
import cl.duoc.zaror.viewmodel.SesionViewModel

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val sesion: SesionViewModel = viewModel()
    val reservasVm: ReservasViewModel = viewModel()
    val finanzasVm: FinanzasViewModel = viewModel()

    NavHost(navController = nav, startDestination = "login") {
        composable("login") {
            LoginScreen { nombre, rol ->
                sesion.ingresar(nombre, rol)
                nav.navigate("home") { popUpTo("login") { inclusive = true } }
            }
        }
        composable("home") {
            HomeScreen(
                sesion = sesion,
                onReservas = { nav.navigate("reservas") },
                onFinanzas = { nav.navigate("finanzas") },
                onSalir = {
                    sesion.salir()
                    nav.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            )
        }
        composable("reservas") {
            ReservasScreen(sesion, reservasVm, onVolver = { nav.popBackStack() })
        }
        composable("finanzas") {
            FinanzasScreen(sesion, finanzasVm, onVolver = { nav.popBackStack() })
        }
    }
}
