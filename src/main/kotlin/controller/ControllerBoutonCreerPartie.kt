package controller

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.util.Duration
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.EtatJeu
import model.Game
import view.InGame
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*


class ControllerBoutonCreerPartie(game: Game, vueInGame: InGame, primaryStage: Stage) : EventHandler<ActionEvent> {
    private var game :Game
    private var vueInGame : InGame
    private var primaryStage : Stage
    private val port = generateUniquePort(5000,6000)
    init {
        this.game = game
        this.primaryStage = primaryStage
        this.vueInGame = vueInGame
    }
    override fun handle(event: ActionEvent?) {
        // Convertissez l'objet Game en JSON
        val gameJson = Json.encodeToString(game)

// Convertissez le JSON en tableau d'octets pour l'envoi du paquet
        val gameData = gameJson.toByteArray()

// Créez le paquet à envoyer
        val socket = DatagramSocket(port)
        val localhost = InetAddress.getLocalHost()
        val packetToSend = DatagramPacket(gameData, gameData.size, localhost, port)

// Envoyez le paquet
        socket.send(packetToSend)
        lateinit var timeline: Timeline

        timeline = Timeline(KeyFrame(Duration.seconds(1.0), {
            if (game.getEtat() == EtatJeu.JEU_FINI) {
                timeline.stop()
                TODO("Implementer dialog box fin de partie")


            } else {
                ControllerBoucleJeu(game,port, vueInGame, primaryStage).mainLoop()
            }

        }))
        timeline.cycleCount = Timeline.INDEFINITE
        timeline.play()

    }

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

}