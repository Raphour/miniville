package controller

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
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
    game: Game, vueRejoindre: VueRejoindrePartie, vueInGame: InGame, vueAccueil: VueAccueil, primaryStage: Stage
) : EventHandler<ActionEvent> {
    private var primaryStage: Stage
    private var game: Game
    private var vueInGame: InGame
    private var vueRejoindre: VueRejoindrePartie
    private var vueAccueil: VueAccueil
    private var thread: Thread? = null


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
        var idJoueur: Int? = null

        var joueur: Joueur? = null

        val socket = DatagramSocket()

        // Envoi d'une demande de rejoindre la partie au serveur
        val message = "JOIN${vueAccueil.getNomJoueur().padStart(10)}"
        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
        socket.send(sendPacket)

        thread = Thread {

            // Attente de la réponse du serveur
            val receiveData = ByteArray(1024)
            val receivePacket = DatagramPacket(receiveData, receiveData.size)
            socket.receive(receivePacket)

            val response = String(receivePacket.data, 0, receivePacket.length)


            println(response.subSequence(0, 4))
            println(response.subSequence(4, response.length))
            if (response.subSequence(1, response.length) == "JOIN_ACCEPTED") {
                idJoueur = response[0].toString().toInt()
                joueur = Joueur(idJoueur!!, vueAccueil.getNomJoueur())
                println("Vous avez rejoint la partie avec succès.")
                Platform.runLater { primaryStage.scene = Scene(vueInGame) }
                // Ajoutez ici la logique de jeu pour le joueur qui a rejoint la partie


            } else if (joueur != null && response.subSequence(0, 4) == "GAME") {
                game = deserializeJsonToGame(response.subSequence(4, response.length).toString())

                    for (joueurAjoute in game.getListeJoueurs()) {
                        Platform.runLater {
                        vueInGame.addPlayerToGrid(
                            joueurAjoute.getNom(),
                            0,
                            joueur!!.getId(),
                            joueurAjoute.getId(),
                            3,
                            getImageViewList(joueurAjoute)
                        )
                    }

                }

            }



        }
        thread?.start()


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
                val imageView = ImageView(carte.getImageView()) // Créez l'ImageView en utilisant l'image de la carte
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

    private fun deserializeJsonToGame(json: String): Game {
        return Json.decodeFromString(json)
    }

    fun getImageViewList(joueur: Joueur): MutableList<ImageView> {
        val cartesImageViewList: MutableList<ImageView> = joueur.main.map { carte ->
            val imageView = ImageView(carte.getImageView()) // Créez l'ImageView en utilisant l'image de la carte
            // Effectuez ici des configurations supplémentaires sur l'ImageView si nécessaire
            imageView
        }.toMutableList()
        return cartesImageViewList
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