package Controladores

import Conexion
import Models.MostrarPartida
import Models.PersonajePartida

object PartidaControlador {

    fun postTablero(tableroOculto:String,tableroJugador:String,idUsu:Int?):Boolean{
        Conexion.abrirConexion()
        var resultado = 0

        try {
            val consulta= "INSERT INTO partida (idPers,tableroOculto,tableroJugador) VALUES(?,?,?)"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            if (idUsu != null) {
                psmt?.setInt(1,idUsu)
            }
            psmt?.setString(2,tableroOculto)
            psmt?.setString(3,tableroJugador)

            resultado= psmt?.executeUpdate() ?:0
            psmt?.close()

        }catch (e:Exception){
            println("Error al insertar el usuario:${e.message}")
            e.printStackTrace()
        }finally {
            Conexion.cerrarConexion()
        }
        return resultado > 0
    }

    fun getUltimoIdPartida(): Int {
        Conexion.abrirConexion()
        var idPartida: Int =0

        try {
            val consulta = "SELECT MAX(id) FROM partida"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                idPartida = resultSet.getInt(1)
            }

            psmt?.close()

        } catch (e: Exception) {
            println("Error al obtener el último ID de partida: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return idPartida
    }

    fun postPersonajes(personajes:ArrayList<PersonajePartida>):Boolean{
        Conexion.abrirConexion()
        var resultado = 0

        try {
            val consulta =
                "INSERT INTO PARTIDAPERSONAJE (id, idPers, capacidad, estado) VALUES (?, ?, ?, ?)"
            val psmt = Conexion.conexion?.prepareStatement(consulta)

            for (personaje in personajes) {
                psmt?.setInt(1, personaje.id)
                psmt?.setInt(2, personaje.idPers)
                psmt?.setInt(3, personaje.capacidad)
                psmt?.setInt(4, personaje.estado)

                resultado += psmt?.executeUpdate() ?: 0
                psmt?.clearParameters()
            }

            psmt?.close()

        } catch (e: Exception) {
            println("Error al insertar los personajes: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }
        return resultado > 0
    }

    fun getTableroOculto(idPartida: Int): String? {
        Conexion.abrirConexion()

        try {
            val consulta = "SELECT tablero_oculto FROM PARTIDA WHERE id_part = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setInt(1, idPartida)
            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                return resultSet.getString("tablero_oculto")
            }
        } catch (e: Exception) {
            println("Error al obtener el tablero oculto: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return null
    }

    fun getTableroJugador(idPartida: Int): String? {
        Conexion.abrirConexion()

        try {
            val consulta = "SELECT tablero_jugador FROM PARTIDA WHERE id_part = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setInt(1, idPartida)
            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                return resultSet.getString("tablero_jugador")
            }
        } catch (e: Exception) {
            println("Error al obtener el tablero del jugador: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return null
    }

    fun putTableroJugador(idPartida: Int, tableroJugador: String) {
        Conexion.abrirConexion()

        try {
            val consulta = "UPDATE PARTIDA SET tablero_jugador = ? WHERE id_part = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)

            if (psmt != null) {
                psmt.setString(1, tableroJugador)
                psmt.setInt(2, idPartida)

                val filasAfectadas = psmt.executeUpdate()

                if (filasAfectadas > 0) {
                    println("Tablero del Jugador actualizado correctamente")
                } else {
                    println("No se pudo actualizar el Tablero del Jugador")
                }

                psmt.close()
            }
        } catch (e: Exception) {
            println("Error al actualizar el Tablero del Jugador: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }
    }

    fun getPersonajes(idPartida: Int): ArrayList<PersonajePartida> {
        val personajes = ArrayList<PersonajePartida>()

        Conexion.abrirConexion()

        try {
            val consulta = "SELECT * FROM PARTIDAPERSONAJE WHERE id = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setInt(1, idPartida)

            val resultSet = psmt?.executeQuery()

            if (resultSet != null) {
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val idPers = resultSet.getInt("idPers")
                    val capacidad = resultSet.getInt("capacidad")
                    val estado = resultSet.getInt("estado")

                    val personaje = PersonajePartida(id,idPers, capacidad, estado)
                    personajes.add(personaje)
                }
            }
        } catch (e: Exception) {
            println("Error al obtener los personajes: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return personajes
    }

    fun putCapacidadPersonaje(idPersonaje: Int,id: Int, nuevaCapacidad: Int): Boolean {
        Conexion.abrirConexion()
        var resultado = false

        try {
            val consulta = "UPDATE PARTIDAPERSONAJE SET capacidad = ? WHERE id_pers = ? AND id=?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setInt(1, nuevaCapacidad)
            psmt?.setInt(2, idPersonaje)
            psmt?.setInt(3, id)

            resultado = (psmt?.executeUpdate() ?: 0) > 0
            psmt?.close()
        } catch (e: Exception) {
            println("Error al actualizar la capacidad del personaje: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return resultado
    }

    fun putEstadoPersonaje(idPersonaje: Int,id: Int, nuevoEstado: Int): Boolean {
        Conexion.abrirConexion()
        var resultado = false

        try {
            val consulta = "UPDATE PARTIDAPERSONAJE SET estado = ? WHERE id_pers = ? AND id_part=?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            psmt?.setInt(1, nuevoEstado)
            psmt?.setInt(2, idPersonaje)
            psmt?.setInt(3, id)

            resultado = (psmt?.executeUpdate() ?: 0) > 0
            psmt?.close()
        } catch (e: Exception) {
            println("Error al actualizar el estado del personaje: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return resultado
    }

    fun putEstadoPartida(id: Int, estado: Int) {
        Conexion.abrirConexion()

        try {
            val consulta = "UPDATE PARTIDA SET estado = ? WHERE id_part = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)

            psmt?.setInt(1, estado)
            psmt?.setInt(2, id)

            val resultado = psmt?.executeUpdate()

            if (resultado != null && resultado > 0) {
                println("Estado de la partida actualizado con éxito.")
            } else {
                println("No se pudo actualizar el estado de la partida.")
            }

            psmt?.close()
        } catch (e: Exception) {
            println("Error al actualizar el estado de la partida: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }
    }

    fun getPartidas(idUsu: Int?):ArrayList<MostrarPartida>{
        val partidas = ArrayList<MostrarPartida>()
        Conexion.abrirConexion()

        try{
            val consulta = "SELECT id,tablero_jugador,estado FROM PARTIDA WHERE id_usu=?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)
            if (idUsu != null) {
                psmt?.setInt(1, idUsu)
            }

            val resultSet = psmt?.executeQuery()

            if(resultSet!=null){
                while (resultSet.next()){
                    val idPart = resultSet.getInt("id_part")
                    val tableroJugador = resultSet.getString("tablero_jugador")
                    val estado = resultSet.getInt("estado")
                    val partidaMostrar = MostrarPartida(idPart,tableroJugador,estado)
                    partidas.add(partidaMostrar)
                }
            }
        }catch (e:Exception){
            println("Error al obtener las partidas: ${e.message}")
        }finally {
            Conexion.cerrarConexion()
        }

        return partidas
    }

    fun tienePartida(idUsu: Int?, id: Int): Boolean {
        Conexion.abrirConexion()

        try {
            val consulta = "SELECT COUNT(*) FROM PARTIDA WHERE id_usu = ? AND id = ?"
            val psmt = Conexion.conexion?.prepareStatement(consulta)

            psmt?.setInt(1, idUsu ?: -1)
            psmt?.setInt(2, id)

            val resultSet = psmt?.executeQuery()

            if (resultSet?.next() == true) {
                val cantidad = resultSet.getInt(1)
                return cantidad > 0
            }

            psmt?.close()
        } catch (e: Exception) {
            println("Error al verificar si el usuario tiene la partida: ${e.message}")
            e.printStackTrace()
        } finally {
            Conexion.cerrarConexion()
        }

        return false
    }

}

