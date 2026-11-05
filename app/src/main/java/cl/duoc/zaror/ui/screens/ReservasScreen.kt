package cl.duoc.zaror.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.zaror.data.EstadoReserva
import cl.duoc.zaror.data.Reserva
import cl.duoc.zaror.data.Rol
import cl.duoc.zaror.viewmodel.ReservasViewModel
import cl.duoc.zaror.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    sesion: SesionViewModel,
    vm: ReservasViewModel,
    onVolver: () -> Unit
) {
    val rol by sesion.rol.collectAsState()
    val nombre by sesion.nombre.collectAsState()
    val reservas by vm.reservas.collectAsState()
    val error by vm.error.collectAsState()
    var mostrarForm by remember { mutableStateOf(false) }

    val puedeSolicitar = rol == Rol.VECINO || rol == Rol.DIRECTIVA
    val puedeGestionar = rol == Rol.DIRECTIVA

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservas") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } }
            )
        },
        floatingActionButton = {
            if (puedeSolicitar) {
                FloatingActionButton(onClick = {
                    vm.limpiarError()
                    mostrarForm = true
                }) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    ) { inner ->
        Column(Modifier.padding(inner).padding(horizontal = 16.dp)) {
            if (!mostrarForm && error != null) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                    TextButton(onClick = vm::limpiarError) { Text("Cerrar") }
                }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reservas, key = { it.id }) { reserva ->
                    ReservaCard(
                        reserva = reserva,
                        puedeGestionar = puedeGestionar,
                        onConfirmar = { vm.confirmar(reserva.id) },
                        onRechazar = { vm.rechazar(reserva.id) }
                    )
                }
            }
        }
    }

    if (mostrarForm) {
        FormReserva(
            error = error,
            onCancelar = { mostrarForm = false },
            onEnviar = { fecha, ini, fin, motivo ->
                vm.solicitar(nombre, fecha, ini, fin, motivo) { mostrarForm = false }
            }
        )
    }
}

@Composable
private fun ReservaCard(
    reserva: Reserva,
    puedeGestionar: Boolean,
    onConfirmar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(reserva.motivo, style = MaterialTheme.typography.titleMedium)
            Text("${reserva.fecha} · ${reserva.horaInicio} a ${reserva.horaFin}")
            Text("Solicitante: ${reserva.vecino}")
            Text("Estado: ${reserva.estado.etiqueta}", style = MaterialTheme.typography.labelLarge)
            if (puedeGestionar && reserva.estado == EstadoReserva.PENDIENTE) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Button(onClick = onConfirmar) { Text("Confirmar") }
                    OutlinedButton(onClick = onRechazar) { Text("Rechazar") }
                }
            }
        }
    }
}

@Composable
private fun FormReserva(
    error: String?,
    onCancelar: () -> Unit,
    onEnviar: (fecha: String, inicio: String, fin: String, motivo: String) -> Unit
) {
    var fecha by remember { mutableStateOf("") }
    var inicio by remember { mutableStateOf("") }
    var fin by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Solicitar arriendo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(fecha, { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") }, singleLine = true)
                OutlinedTextField(inicio, { inicio = it }, label = { Text("Hora inicio (HH:mm)") }, singleLine = true)
                OutlinedTextField(fin, { fin = it }, label = { Text("Hora término (HH:mm)") }, singleLine = true)
                OutlinedTextField(motivo, { motivo = it }, label = { Text("Motivo") })
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEnviar(fecha.trim(), inicio.trim(), fin.trim(), motivo) }) { Text("Enviar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
