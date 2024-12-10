import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

object Conexion {
    var conexion: Connection? = null
    var sentenciaSQL: Statement? = null

    fun abrirConexion(): Int {
        var cod = 0
        try {
            val controlador = "com.mysql.cj.jdbc.Driver"
            val URL_BD = "jdbc:mysql://" + Constantes.server+":"+Constantes.puerto+"/" + Constantes.bbdd


            Class.forName(controlador)

            conexion = DriverManager.getConnection(URL_BD, Constantes.usuario, Constantes.password)
            sentenciaSQL = conexion!!.createStatement()
        } catch (e: Exception) {
            System.err.println("Exception: " + e.message)
            cod = -1
        }
        return cod
    }

    fun cerrarConexion(): Int {
        var cod = 0
        try {
            conexion!!.close()
        } catch (ex: SQLException) {
            cod = -1
        }
        return cod
    }
}