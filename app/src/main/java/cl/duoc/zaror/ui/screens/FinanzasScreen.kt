package cl.duoc.zaror.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.zaror.data.Movimiento
import cl.duoc.zaror.data.Rol
import cl.duoc.zaror.data.TipoMovimiento
import cl.duoc.zaror.data.comoPesos
import cl.duoc.zaror.viewmodel.FinanzasViewModel
import cl.duoc.zaror.viewmodel.ResumenFinanciero
import cl.duoc.zaror.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanzasScreen(
    sesion: SesionViewModel,
    vm: FinanzasViewModel,
    onVolver: () -> Unit
) {
    val rol by sesion.rol.collectAsState()
    val movimientos by vm.movimientos.collectAsState()
    val resumen by vm.resumen.collectAsState()
    val error by vm.error.collectAsState()
    var mostrarForm by remember { mutableStateOf(false) }

    val puedeRegistrar = rol == Rol.TESORERIA

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finanzas") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } }
            )
        },
        floatingActionButton = {
            if (puedeRegistrar) {
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
            ResumenCard(resumen)
            Text(
                "Movimientos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(movimientos.sortedByDescending { it.fecha }, key = { it.id }) { MovimientoItem(it) }
            }
        }
    }

    if (mostrarForm) {
        FormMovimiento(
            error = error,
            onCancelar = { mostrarForm = false },
            onGuardar = { tipo, concepto, monto, fecha ->
                vm.registrar(tipo, concepto, monto, fecha) { mostrarForm = false }
            }
        )
    }
}

@Composable
private fun ResumenCard(resumen: ResumenFinanciero) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium)
            Text("Ingresos: ${resumen.ingresos.comoPesos()}")
            Text("Gastos: ${resumen.gastos.comoPesos()}")
            Text("Saldo: ${resumen.saldo.comoPesos()}", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun MovimientoItem(m: Movimiento) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(m.concepto, style = MaterialTheme.typography.titleSmall)
                Text("${m.tipo.etiqueta} · ${m.fecha}", style = MaterialTheme.typography.bodySmall)
            }
            val signo = if (m.tipo == TipoMovimiento.INGRESO) "+" else "-"
            Text("$signo${m.monto.comoPesos()}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormMovimiento(
    error: String?,
    onCancelar: () -> Unit,
    onGuardar: (tipo: TipoMovimiento, concepto: String, monto: String, fecha: String) -> Unit
) {
    var tipo by remember { mutableStateOf(TipoMovimiento.INGRESO) }
    var concepto by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar movimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoMovimiento.values().forEach { t ->
                        FilterChip(
                            selected = tipo == t,
                            onClick = { tipo = t },
                            label = { Text(t.etiqueta) }
                        )
                    }
                }
                OutlinedTextField(concepto, { concepto = it }, label = { Text("Concepto") })
                OutlinedTextField(
                    monto, { monto = it },
                    label = { Text("Monto (CLP)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(fecha, { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") }, singleLine = true)
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onGuardar(tipo, concepto, monto.trim(), fecha.trim()) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
