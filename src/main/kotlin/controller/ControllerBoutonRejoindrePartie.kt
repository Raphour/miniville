package controller

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.serialization.json.Json
import model.Game
import model.Joueur
import view.InGame
import view.VueRejoindrePartie
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*


class ControllerBoutonRejoindrePartie(
    game: Game,
    vueRejoindre: VueRejoindrePartie,
    vueInGame: InGame,
    primaryStage: Stage
) : EventHandler<ActionEvent> {
    private var primaryStage: Stage
    private val game: Game
    private var vueInGame: InGame
    private var vueRejoindre: VueRejoindrePartie

    // Port d'écoute pour les paquets
    private val port = generateUniquePort(5000, 6000)

    // Taille maximale du buffer pour les paquets reçus
    private val bufferSize = 1024

    // Créer une nouvelle partie
    init {
        this.primaryStage = primaryStage
        this.game = game
        this.vueInGame = vueInGame
        this.vueRejoindre = vueRejoindre

    }

    override fun handle(event: ActionEvent?) {
        if (partieExistante()) {
            val nom = vueRejoindre.getNomField()
            if (nom != "") {


                val buffer = ByteArray(bufferSize)
                val packet = DatagramPacket(buffer, buffer.size)
                val socket = DatagramSocket(port)
                val listeJoueurs: MutableList<Joueur>?

                socket.receive(packet) // Réception du paquet


                // Traitez le paquet selon vos besoins
                val receivedData = String(packet.data, 0, packet.length)
                val receivedGame: Game? = try {
                    Json.decodeFromString(receivedData)
                } catch (e: Exception) {
                    null
                }

                receivedGame?.let {
                    // Traitez l'objet Game selon vos besoins
                    println("Game reçu : $it")

                    // Stockez la liste de joueurs dans la variable joueurList
                    listeJoueurs = it.getListeJoueurs()
                    val idJoueur = listeJoueurs.size
                    val joueur = Joueur(idJoueur, nom)
                    game.setPlayer(joueur)
                    game.addPlayerToGame(joueur)


                }

                for (joueurAjoute in game.getListeJoueurs()) {
                    val cartesImageViewList: MutableList<ImageView> = joueurAjoute.main.map { carte ->
                        val imageView =
                            ImageView(carte.getImageView()) // Créez l'ImageView en utilisant l'image de la carte
                        // Effectuez ici des configurations supplémentaires sur l'ImageView si nécessaire
                        imageView
                    }.toMutableList()
                    fun createUniqueMutableList(joueurs: List<Joueur>): MutableList<Int> {
                        val joueursIds = joueurs.map { it.getId() }
                        val uniqueList = mutableListOf<Int>()
                        for (i in 1..4) {
                            if (!joueursIds.contains(i)) {
                                uniqueList.add(i)
                            }
                        }
                        return uniqueList
                    }
                    vueRejoindre.setListeId(createUniqueMutableList(game.getListeJoueurs()))

                    vueInGame.addPlayerToGrid(
                        joueurAjoute.getNom(),
                        joueurAjoute.getMainMonument().count { it.getEtat() },
                        game.getJoueur().getId(),
                        joueurAjoute.getId(),
                        joueurAjoute.getBourse(),
                        cartesImageViewList
                    )
                }

            }

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