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
import view.VueAccueil
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
    vueAccueil: VueAccueil,
    primaryStage: Stage
) : EventHandler<ActionEvent> {
    private var primaryStage: Stage
    private val game: Game
    private var vueInGame: InGame
    private var vueRejoindre: VueRejoindrePartie
    private var vueAccueil: VueAccueil


    // Taille maximale du buffer pour les paquets reçus
    private val bufferSize = 1024

    private val serverAddress: InetAddress = InetAddress.getByName("127.0.0.1")
    private val serverPort: Int = 5000


    // Créer une nouvelle partie
    init {
        this.primaryStage = primaryStage
        this.game = game
        this.vueInGame = vueInGame
        this.vueRejoindre = vueRejoindre
        this.vueAccueil = vueAccueil

    }

    override fun handle(event: ActionEvent?) {

        val socket = DatagramSocket()

        // Envoi d'une demande de rejoindre la partie au serveur
        val message = "JOIN${vueAccueil.getNomJoueur().padStart(10)}"
        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
        socket.send(sendPacket)

        // Attente de la réponse du serveur
        val receiveData = ByteArray(1024)
        val receivePacket = DatagramPacket(receiveData, receiveData.size)
        socket.receive(receivePacket)

        val response = String(receivePacket.data, 0, receivePacket.length)



        if (response.subSequence(1, response.length) == "JOIN_ACCEPTED") {
            val idJoueur = response[0].toString().toInt()
            val joueur = Joueur(idJoueur, vueAccueil.getNomJoueur())
            println("Vous avez rejoint la partie avec succès.")
            // Ajoutez ici la logique de jeu pour le joueur qui a rejoint la partie
            miseEnPlacePlateauJoueur(joueur)
        } else {
            println("Impossible de rejoindre la partie.")
            // Ajoutez ici la logique de gestion de l'échec de la demande de rejoindre la partie
        }

        val alert = Alert(AlertType.CONFIRMATION)
        alert.title = "Problème de partie"
        alert.headerText = "Il semble n'y avoir aucune partie en cours"
        alert.contentText = "Voulez vous créer une partie?"


    }

    /**
     * Inqique si une partie est déja en cours
     *
     * @return un booléen qui inqique si une partie est déja en cours
     */
    private fun partieExistante(): Boolean {

        val socket = DatagramSocket(0)
        val buffer = ByteArray(bufferSize)
        val packet = DatagramPacket(buffer, buffer.size)

        // Essayez de recevoir un paquet
        socket.soTimeout = 5000 // Timeout de réception (1 seconde)

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

    private fun miseEnPlacePlateauJoueur(joueur: Joueur) {
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
                joueur.getId(),
                joueurAjoute.getId(),
                joueurAjoute.getBourse(),
                cartesImageViewList
            )
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