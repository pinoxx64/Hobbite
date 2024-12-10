package Models

import kotlinx.serialization.Serializable

@Serializable
data class MostrarUsuario (val idUsu:Int, val Gmail:String,val contrasena:String,val nombre:String,val admin:Int)