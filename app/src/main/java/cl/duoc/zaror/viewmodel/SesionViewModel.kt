package cl.duoc.zaror.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.zaror.data.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SesionViewModel : ViewModel() {

    private val _rol = MutableStateFlow<Rol?>(null)
    val rol: StateFlow<Rol?> = _rol.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    fun ingresar(nombre: String, rol: Rol) {
        _nombre.value = nombre
        _rol.value = rol
    }

    fun salir() {
        _rol.value = null
        _nombre.value = ""
    }
}
