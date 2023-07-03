package controller

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.stage.Stage
import javafx.util.Duration
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.EtatJeu
import model.Game
import model.Joueur
import view.InGame
import view.VueCreerPartie
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*


class ControllerBoutonCreerPartie(game: Game,vueCreerPartie: VueCreerPartie, vueInGame: InGame, primaryStage: Stage) : EventHandler<ActionEvent> {
    private var game: Game
    private var vueInGame: InGame
    private var vueCreerPartie : VueCreerPartie
    private var primaryStage: Stage
    private val port = DatagramSocket(0).localPort

    init {
        this.game = game
        this.primaryStage = primaryStage
        this.vueInGame = vueInGame
        this.vueCreerPartie = vueCreerPartie
    }

    override fun handle(event: ActionEvent?) {

        val sceneCreerPartie = Scene(vueCreerPartie,1080.0, 700.0)
        primaryStage.scene = sceneCreerPartie

        val joueur = Joueur(1,"Baguette")
        val cartesImageViewList: MutableList<ImageView> = joueur.main.map { carte ->
            val imageView = ImageView(carte.getImageView()) // Créez l'ImageView en utilisant l'image de la carte
            // Effectuez ici des configurations supplémentaires sur l'ImageView si nécessaire
            imageView
        }.toMutableList()

        vueInGame.addPlayerToGrid(
            joueur.getNom(),
            joueur.getMainMonument().count { it.getEtat() },
            joueur.getId(),
            joueur.getId(),
            joueur.getBourse(),
            cartesImageViewList
        )

        game.setPlayer(Joueur(0,"Baguette"))

        println("ENCODE TO STRING")
        // Convertissez l'objet Game en JSON
        val gameJson = Json.encodeToString(game)

// Convertissez le JSON en tableau d'octets pour l'envoi du paquet
        val gameData = gameJson.toByteArray()

// Créez le paquet à envoyer
        val socket = DatagramSocket(0)
        val localhost = InetAddress.getLocalHost()
        val packetToSend = DatagramPacket(gameData, gameData.size, localhost, socket.localPort)


// Envoyez le paquet
        socket.send(packetToSend)
        lateinit var timeline: Timeline

        println("TIMELINE")

        timeline = Timeline(KeyFrame(Duration.seconds(1.0), {
            println("WOW une timeline")
            if (game.getEtat() == EtatJeu.JEU_FINI) {
                timeline.stop()



            } else {
                ControllerBoucleJeu(game, socket, vueInGame, primaryStage).mainLoop()
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