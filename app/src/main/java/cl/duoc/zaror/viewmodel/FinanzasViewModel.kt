package cl.duoc.zaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.zaror.data.Repositorio
import cl.duoc.zaror.data.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ResumenFinanciero(val ingresos: Int, val gastos: Int) {
    val saldo: Int get() = ingresos - gastos
}

class FinanzasViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = Repositorio.crear(app)

    val movimientos = repo.movimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resumen: StateFlow<ResumenFinanciero> = repo.movimientos
        .map { lista ->
            ResumenFinanciero(
                ingresos = lista.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto },
                gastos = lista.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenFinanciero(0, 0))

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val regexFecha = Regex("""\d{4}-\d{2}-\d{2}""")

    /** Valida y guarda el movimiento. Llama a [onExito] solo si se registró correctamente. */
    fun registrar(
        tipo: TipoMovimiento, concepto: String, montoTexto: String, fecha: String,
        onExito: () -> Unit
    ) {
        val monto = montoTexto.toIntOrNull()
        val mensaje = when {
            concepto.isBlank() -> "Indica el concepto"
            monto == null || monto <= 0 -> "El monto debe ser un número mayor a 0"
            !regexFecha.matches(fecha) -> "La fecha debe tener formato AAAA-MM-DD"
            else -> null
        }
        _error.value = mensaje
        if (mensaje != null || monto == null) return
        viewModelScope.launch {
            repo.registrarMovimiento(tipo, concepto.trim(), monto, fecha)
            onExito()
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
