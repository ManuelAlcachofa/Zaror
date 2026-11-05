package cl.duoc.zaror.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: único punto de acceso a los datos.
 * Hoy usa Room (SQLite local); más adelante se le suma Retrofit sin tocar las pantallas.
 */
class Repositorio(
    private val reservaDao: ReservaDao,
    private val movimientoDao: MovimientoDao
) {
    val reservas: Flow<List<Reserva>> = reservaDao.observarTodas()
    val movimientos: Flow<List<Movimiento>> = movimientoDao.observarTodos()

    // ---------- Reservas ----------

    /** Devuelve un mensaje de error, o null si todo salió bien. */
    suspend fun solicitarReserva(
        vecino: String, fecha: String, inicio: String, fin: String, motivo: String
    ): String? {
        if (fin <= inicio) return "La hora de término debe ser posterior a la de inicio"
        if (reservaDao.contarSolapes(fecha, inicio, fin, excluirId = 0) > 0) {
            return "Ya existe un arriendo confirmado en ese horario"
        }
        reservaDao.insertar(Reserva(vecino = vecino, fecha = fecha, horaInicio = inicio, horaFin = fin, motivo = motivo))
        return null
    }

    /** Devuelve un mensaje de error, o null si todo salió bien. */
    suspend fun cambiarEstado(id: Int, estado: EstadoReserva): String? {
        val reserva = reservaDao.buscar(id) ?: return "Reserva no encontrada"
        if (estado == EstadoReserva.CONFIRMADA &&
            reservaDao.contarSolapes(reserva.fecha, reserva.horaInicio, reserva.horaFin, excluirId = id) > 0
        ) {
            return "No se puede confirmar: se solapa con otro arriendo confirmado"
        }
        reservaDao.actualizar(reserva.copy(estado = estado))
        return null
    }

    // ---------- Finanzas ----------

    suspend fun registrarMovimiento(tipo: TipoMovimiento, concepto: String, monto: Int, fecha: String) {
        movimientoDao.insertar(Movimiento(tipo = tipo, concepto = concepto, monto = monto, fecha = fecha))
    }

    companion object {
        fun crear(context: Context): Repositorio {
            val db = AppDatabase.obtener(context)
            return Repositorio(db.reservaDao(), db.movimientoDao())
        }
    }
}
