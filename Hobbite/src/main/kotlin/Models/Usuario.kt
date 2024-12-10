package Models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario (val gmail:String, val contrasena:String, val nombre:String, val admin:Int)