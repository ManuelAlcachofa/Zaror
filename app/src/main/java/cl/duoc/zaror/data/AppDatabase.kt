package cl.duoc.zaror.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Reserva::class, Movimiento::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zaror.db"
                )
                    .addCallback(DatosIniciales)
                    .build()
                    .also { instancia = it }
            }
    }

    /** Se ejecuta solo la primera vez que se crea la base: carga datos FICTICIOS de ejemplo. */
    private object DatosIniciales : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "INSERT INTO reservas (vecino, fecha, horaInicio, horaFin, motivo, estado) VALUES " +
                    "('Vecina Demo 1', '2026-11-07', '15:00', '20:00', 'Cumpleaños', 'CONFIRMADA'), " +
                    "('Vecino Demo 2', '2026-11-14', '10:00', '14:00', 'Reunión de comité', 'PENDIENTE')"
            )
            db.execSQL(
                "INSERT INTO movimientos (tipo, concepto, monto, fecha) VALUES " +
                    "('INGRESO', 'Arriendo sede 07-11', 40000, '2026-11-07'), " +
                    "('GASTO', 'Cuenta de luz', 18500, '2026-11-03'), " +
                    "('GASTO', 'Cuenta de agua', 12300, '2026-11-04')"
            )
        }
    }
}
