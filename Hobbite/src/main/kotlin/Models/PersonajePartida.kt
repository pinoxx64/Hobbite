package Models

import kotlinx.serialization.Serializable

@Serializable
data class PersonajePartida (val id:Int, val idPers:Int, var capacidad:Int, var estado:Int)