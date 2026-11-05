package cl.duoc.zaror.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.zaror.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sesion: SesionViewModel,
    onReservas: () -> Unit,
    onFinanzas: () -> Unit,
    onSalir: () -> Unit
) {
    val nombre by sesion.nombre.collectAsState()
    val rol by sesion.rol.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                actions = { TextButton(onClick = onSalir) { Text("Salir") } }
            )
        }
    ) { inner ->
        Column(Modifier.padding(inner).padding(16.dp)) {
            Text("Hola, $nombre", style = MaterialTheme.typography.headlineSmall)
            Text("Perfil: ${rol?.etiqueta ?: ""}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))

            Card(onClick = onReservas, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Reservas de la sede", style = MaterialTheme.typography.titleMedium)
                    Text("Solicitudes, disponibilidad y confirmaciones")
                }
            }
            Spacer(Modifier.height(12.dp))
            Card(onClick = onFinanzas, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Finanzas y transparencia", style = MaterialTheme.typography.titleMedium)
                    Text("Ingresos, gastos y saldo de la Junta")
                }
            }
        }
    }
}
