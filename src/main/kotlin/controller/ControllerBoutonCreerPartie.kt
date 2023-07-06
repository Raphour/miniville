package controller

import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.stage.Stage
import model.EtatJeu
import model.Game
import model.Joueur
import view.InGame
import view.VueAccueil
import java.io.IOException
import java.net.*
import java.util.*
const val MULTICAST_GROUP = "224.0.0.1"

const val MULTICAST_PORT = 8888

class ControllerBoutonCreerPartie(game: Game, vueAccueil: VueAccueil, vueInGame: InGame, primaryStage: Stage) :
    EventHandler<ActionEvent> {
    private var game: Game
    private var vueInGame: InGame
    private var vueAccueil: VueAccueil
    private var primaryStage: Stage
    private val port = DatagramSocket(0).localPort
    private var thread: Thread? = null


    init {
        this.game = game
        this.primaryStage = primaryStage
        this.vueInGame = vueInGame
        this.vueAccueil = vueAccueil

    }

    override fun handle(event: ActionEvent?) {

        val sceneCreerPartie = Scene(vueInGame, 1080.0, 700.0)
        primaryStage.scene = sceneCreerPartie

        val joueur = Joueur(1, "Baguette")
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

        game.setPlayer(Joueur(0, "Baguette"))
        /**
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
         **/

        // Instance du jeu
        val gameInstance = "GameInstance1"

        // Création du socket multicast pour la découverte
        val multicastSocket = MulticastSocket(MULTICAST_PORT)
        val multicastGroup = InetAddress.getByName(MULTICAST_GROUP)
        multicastSocket.joinGroup(InetSocketAddress(multicastGroup, MULTICAST_PORT), NetworkInterface.getByInetAddress(InetAddress.getLocalHost()))

        // Envoi d'un message pour annoncer la présence de l'instance du jeu
        val message = "GameInstance:$gameInstance".toByteArray()
        val packet = DatagramPacket(message, message.size, multicastGroup, MULTICAST_PORT)
        multicastSocket.send(packet)

        // Attente des messages des autres instances
        val receiveBuffer = ByteArray(1024)
        val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
        while (true) {
            multicastSocket.receive(receivePacket)
            val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
            if (receivedMessage.startsWith("GameInstance:")) {
                val discoveredInstance = receivedMessage.substringAfter("GameInstance:")
                println("Nouvelle instance découverte: $discoveredInstance")
            }
        }


        lateinit var timeline: Timeline

        println("TIMELINE")

        thread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                println("WOW une timeline")
                if (game.etatJeu == EtatJeu.JEU_FINI) {
                    thread?.interrupt()
                    thread = null

                } else {
                    ControllerBoucleJeu(game, socket, vueAccueil, vueInGame, primaryStage).mainLoop()
                }
            }

        }
        thread?.start()


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