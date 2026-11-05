package cl.duoc.zaror.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Rol(val etiqueta: String) {
    VECINO("Vecino/a"),
    DIRECTIVA("Directiva / Secretaría"),
    TESORERIA("Tesorería")
}

enum class EstadoReserva(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    RECHAZADA("Rechazada")
}

@Entity(tableName = "reservas")
data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vecino: String,
    val fecha: String,        // formato AAAA-MM-DD
    val horaInicio: String,   // formato HH:mm
    val horaFin: String,      // formato HH:mm
    val motivo: String,
    val estado: EstadoReserva = EstadoReserva.PENDIENTE
)

enum class TipoMovimiento(val etiqueta: String) {
    INGRESO("Ingreso"),
    GASTO("Gasto")
}

@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: TipoMovimiento,
    val concepto: String,
    val monto: Int,           // pesos chilenos
    val fecha: String         // formato AAAA-MM-DD
)

fun Int.comoPesos(): String =
    "$" + this.toString().reversed().chunked(3).joinToString(".").reversed()
