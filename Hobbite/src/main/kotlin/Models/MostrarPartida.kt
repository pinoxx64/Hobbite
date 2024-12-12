package Models
import kotlinx.serialization.Serializable

@Serializable
data class MostrarPartida (val id:Int,val tableroJugador:String,val estado:Int)