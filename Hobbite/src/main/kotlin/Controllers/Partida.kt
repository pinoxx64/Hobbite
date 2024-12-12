package Controladores

import Modelos.PartidaPersonaje

object Partida {
    val pruebas = charArrayOf('M', 'H', 'F')
    val esfuerzo = intArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50)

    fun generarTableroOculto(size: Int): String {
        val tablero = StringBuilder()

        for (i in 1..size) {
            val pruebaAleatoria = pruebas.random()
            val esfuerzoAleatorio = esfuerzo.random()
            tablero.append("$pruebaAleatoria$esfuerzoAleatorio ")
        }

        return tablero.toString().trim()
    }
    fun generarTableroJugador(size: Int):String{
        val tableroJugador = StringBuilder()

        for (i in 1..size) {
            tableroJugador.append("t ")
        }

        return tableroJugador.toString().trim()
    }

    fun generarPersonajes(idPartida:Int): ArrayList<PartidaPersonaje> {
        val personajes = ArrayList<PartidaPersonaje>()
        for (i in 1..3) {
            personajes.add(
                PartidaPersonaje(
                    id_pers = i,
                    id_part = idPartida,
                    capacidad = 50,
                    estado = 1
                )
            )
        }
        return personajes
    }

    fun destaparCasilla(tableroOculto: Array<String>, tableroJugador: Array<String>, index: Int) {
        if (tableroJugador[index] != "t") {
            println("Casilla ya destapada")
        }

        val elementoOculto = tableroOculto[index]
        tableroJugador[index] = elementoOculto
    }
}