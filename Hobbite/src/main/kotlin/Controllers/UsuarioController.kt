package Controllers

import Conexion
import Models.Login
import Models.MostrarUsuario
import Models.Usuario
import java.sql.SQLException

object UsuarioController {
    fun getUsuarios() : List<MostrarUsuario> {
        Conexion.abrirConexion()
        val usuarios = ArrayList<MostrarUsuario>()
        try {
            val consulta = "SELECT * FROM usuario"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            val resultSet = psmt?.executeQuery() ?: throw SQLException("No hay usuarios")

            while (resultSet.next()) {
                val usuario = MostrarUsuario(
                    resultSet.getInt("idUsu"),
                    resultSet.getString("gmail"),
                    resultSet.getString("contrasena"),
                    resultSet.getString("nombre"),
                    resultSet.getInt("admin")
                )
                usuarios.add(usuario)
            }
        }catch (e: SQLException){
            e.printStackTrace()
        }finally {
            Conexion.cerrarConexion()
        }

        return usuarios
    }

    fun getUsuarioPorId(id: Int?) : Usuario? {
        Conexion.abrirConexion()

        try {
            val consulta = "SELECT * FROM usuario WHERE idUsu = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            if (id != null) {
                psmt?.setInt(1, id)
            }

            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                return Usuario(
                    resultSet.getString("gmail"),
                    resultSet.getString("contrasena"),
                    resultSet.getString("nombre"),
                    resultSet.getInt("admin")
                )
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return null
    }

    fun postUsuario(usuario: Usuario) : Boolean{
        Conexion.abrirConexion()
        var resultado = 0
        try{
            val consulta = "INSERT INTO usuario (gmail,contrasena,nombre,admin) VALUES (?,?,?,?)"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setString(1,usuario.gmail)
            psmt?.setString(2,usuario.contrasena)
            psmt?.setString(3,usuario.nombre)
            psmt?.setInt(4,usuario.admin)

            resultado= psmt?.executeUpdate() ?:0
            psmt?.close()

        }catch (e:SQLException){
            println("Error en el postUsuario:${e.message}")
            e.printStackTrace()
        }finally {
            Conexion.cerrarConexion()
        }
        return resultado > 0
    }

    fun updateUsuario(id: Int, usuario: Usuario) : Boolean{
        Conexion.abrirConexion()

        try {
            val consulta = "UPDATE usuario SET nombre=?, contrasena=? WHERE idUsu=?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setString(1, usuario.nombre)
            psmt?.setString(2, usuario.contrasena)
            psmt?.setInt(3, id)

            val filasActualizadas = psmt?.executeUpdate()

            return filasActualizadas == 1
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return false
    }

    fun deleteUsuario(id: Int) : Boolean{
        Conexion.abrirConexion()

        try {
            val consulta = "DELETE FROM usuario WHERE idUsu = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setInt(1, id)

            val filasAfectadas = psmt?.executeUpdate()

            return filasAfectadas == 1
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return false
    }

    fun login(usuario: Usuario) : Boolean{
        Conexion.abrirConexion()
        var resultado = false

        try {
            val consulta = "SELECT COUNT(*) FROM usuario WHERE gmail=? AND contrasena =?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setString(1, usuario.gmail)
            psmt?.setString(2, usuario.contrasena)

            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                val count = resultSet.getInt(1)
                resultado = count > 0
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return resultado
    }

    fun getIdUsuarioLogeado(login: Login) : Int?{
        var idUsuario: Int? = null

        try {
            Conexion.abrirConexion()

            val consulta = "SELECT id_usu FROM USUARIO WHERE email=? AND contra=?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setString(1, login.gmail)
            psmt?.setString(2, login.contrasena)

            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                idUsuario = resultSet.getInt("id_usu")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return idUsuario
    }

    fun admin(login: Login): Boolean{
        Conexion.abrirConexion()
        var resultado = false

        try {
            val consulta = "SELECT COUNT(*) FROM USUARIO WHERE gmail=? AND contrasena =? AND admin=1"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setString(1, login.gmail)
            psmt?.setString(2, login.contrasena)

            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                val count = resultSet.getInt(1)
                resultado = count > 0
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return resultado
    }
}