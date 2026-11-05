package cl.duoc.zaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.zaror.data.EstadoReserva
import cl.duoc.zaror.data.Repositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReservasViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = Repositorio.crear(app)

    val reservas = repo.reservas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val regexFecha = Regex("""\d{4}-\d{2}-\d{2}""")
    private val regexHora = Regex("""([01]\d|2[0-3]):[0-5]\d""")

    /** Valida y guarda la solicitud. Llama a [onExito] solo si se registró correctamente. */
    fun solicitar(
        vecino: String, fecha: String, inicio: String, fin: String, motivo: String,
        onExito: () -> Unit
    ) {
        val mensaje = when {
            !regexFecha.matches(fecha) -> "La fecha debe tener formato AAAA-MM-DD"
            !regexHora.matches(inicio) || !regexHora.matches(fin) -> "Las horas deben tener formato HH:mm"
            motivo.isBlank() -> "Indica el motivo del arriendo"
            else -> null
        }
        if (mensaje != null) {
            _error.value = mensaje
            return
        }
        viewModelScope.launch {
            val errorRepo = repo.solicitarReserva(vecino, fecha, inicio, fin, motivo.trim())
            _error.value = errorRepo
            if (errorRepo == null) onExito()
        }
    }

    fun confirmar(id: Int) {
        viewModelScope.launch { _error.value = repo.cambiarEstado(id, EstadoReserva.CONFIRMADA) }
    }

    fun rechazar(id: Int) {
        viewModelScope.launch { _error.value = repo.cambiarEstado(id, EstadoReserva.RECHAZADA) }
    }

    fun limpiarError() {
        _error.value = null
    }
}
