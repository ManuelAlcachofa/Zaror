package cl.duoc.zaror.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Query("SELECT * FROM reservas ORDER BY fecha, horaInicio")
    fun observarTodas(): Flow<List<Reserva>>

    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun buscar(id: Int): Reserva?

    /** Cuenta arriendos CONFIRMADOS que se cruzan con el horario indicado (ignorando la reserva [excluirId]). */
    @Query(
        """SELECT COUNT(*) FROM reservas
           WHERE estado = 'CONFIRMADA' AND fecha = :fecha
           AND horaInicio < :fin AND horaFin > :inicio
           AND id != :excluirId"""
    )
    suspend fun contarSolapes(fecha: String, inicio: String, fin: String, excluirId: Int): Int

    @Insert
    suspend fun insertar(reserva: Reserva)

    @Update
    suspend fun actualizar(reserva: Reserva)
}

@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC, id DESC")
    fun observarTodos(): Flow<List<Movimiento>>

    @Insert
    suspend fun insertar(movimiento: Movimiento)
}
