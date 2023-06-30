package controller

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import model.Game
import view.CreerPartie
import view.InGame
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*


class ControllerBoutonRejoindrePartie(game: Game, vueInGame: InGame, primaryStage: Stage) : EventHandler<ActionEvent> {
    private var primaryStage : Stage
    private val game : Game
    private var vueInGame : InGame
    // Port d'écoute pour les paquets
    private val port = generateUniquePort(5000, 6000)

    // Taille maximale du buffer pour les paquets reçus
    private val bufferSize = 1024

    // Créer une nouvelle partie
    init {
        this.primaryStage = primaryStage
        this.game = game
        this.vueInGame = vueInGame

    }
    override fun handle(event: ActionEvent?) {
        if (partieExistante()) {
            creerPartie(port)
            val vue = CreerPartie()
            vue.boutonCreerPartie.onAction = ControllerBoutonCreerPartie(game, vueInGame, primaryStage)
            val scene = Scene(vue)

            primaryStage.scene = scene

        } else {
            val alert = Alert(AlertType.CONFIRMATION)
            alert.title = "Problème de partie"
            alert.headerText = "Il semble n'y avoir aucune partie en cours"
            alert.contentText = "Voulez vous créer une partie?"

            val result = alert.showAndWait()
            if (result.get() == ButtonType.OK) {
                creerPartie(port)
            }
        }
    }

    /**
     * Inqique si une partie est déja en cours
     *
     * @return un booléen qui inqique si une partie est déja en cours
     */
    private fun partieExistante(): Boolean {

        val socket = DatagramSocket(port)
        val buffer = ByteArray(bufferSize)
        val packet = DatagramPacket(buffer, buffer.size)

        // Essayez de recevoir un paquet
        socket.soTimeout = 1000 // Timeout de réception (1 seconde)

        return try {
            socket.receive(packet)
            socket.close()
            // Un paquet a été reçu, donc une partie existe déjà
            true
        } catch (e: Exception) {
            // Aucun paquet reçu, aucune partie existante
            false
        }
    }

    /**
     * Indique si un port est disponible ou non
     *
     * @param port
     * @return Un booléen decrivant la disponibilité du port
     */
    private fun isPortAvailable(port: Int): Boolean {
        return try {
            ServerSocket(port).use {
                // Le port est disponible
                true
            }
        } catch (e: IOException) {
            // Le port n'est pas disponible
            false
        }
    }

    /**
     * Renvoie un port unique non utilisé par un autre service
     *
     * @param minPort
     * @param maxPort
     * @return Un port non utilisé
     */
    private fun generateUniquePort(minPort: Int, maxPort: Int): Int {
        val random = Random()
        var port = random.nextInt(maxPort - minPort + 1) + minPort
        while (!isPortAvailable(port)) {
            port = random.nextInt(maxPort - minPort + 1) + minPort
        }
        return port
    }

    private fun creerPartie(port: Int) {
        val socket = DatagramSocket()
        val message = "Nouvelle partie".toByteArray()
        val packet = DatagramPacket(message, message.size, InetAddress.getLocalHost(), port)

        // Envoyer le paquet
        socket.send(packet)
        socket.close()

        println("Nouvelle partie créée !")
    }



}