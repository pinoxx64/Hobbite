package Models

import kotlinx.serialization.Serializable

@Serializable
data class MostrarUsuario (val idUsu:Int, val gmail:String,val contrasena:String,val nombre:String,val admin:Int)