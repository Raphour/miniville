package controller

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.EtatJeu
import model.Game
import model.Joueur
import view.InGame
import view.VueAccueil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class ControllerBoutonCreerPartie(game: Game, vueAccueil: VueAccueil, vueInGame: InGame, primaryStage: Stage) :
    EventHandler<ActionEvent> {
    private var game: Game
    private var vueInGame: InGame
    private var vueAccueil: VueAccueil
    private var primaryStage: Stage
    private val serverPort: Int = 5000
    private val clients: MutableMap<InetAddress, Int> = mutableMapOf()
    private var thread: Thread? = null


    init {
        this.game = game
        this.primaryStage = primaryStage
        this.vueInGame = vueInGame
        this.vueAccueil = vueAccueil

    }

    override fun handle(event: ActionEvent?) {

        if (vueAccueil.getNomJoueur() == "") {
            val alert = Alert(AlertType.WARNING)
            alert.title = "Attention !"
            alert.headerText = "Vous n'avez pas renseigné de nom"
            alert.contentText = "Entrez votre nom"

            alert.showAndWait()
        } else {
            val sceneCreerPartie = Scene(vueInGame, 1080.0, 700.0)
            primaryStage.scene = sceneCreerPartie

            val joueur = Joueur(0, vueAccueil.getNomJoueur())
            game.addPlayerToGame(joueur)
            game.setActualPlayerById(0)


            vueInGame.addPlayerToGrid(
                joueur.getNom(),
                joueur.getMainMonument().count { it.getEtat() },
                joueur.getId(),
                joueur.getId(),
                joueur.getBourse(),
                getImageViewList(joueur)
            )

            val socket = DatagramSocket(serverPort)

            println("Serveur en attente de connexions...")

            thread = Thread {
                if (game.etatJeu == EtatJeu.ATTENTE_JOUEURS) {
                    val receiveData = ByteArray(1024)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)

                    val clientAddress = receivePacket.address
                    val clientPort = receivePacket.port
                    var clientRequest = String(receivePacket.data, 0, receivePacket.length)

                    val nomJoueurDemande = clientRequest.subSequence(3, clientRequest.length)
                    clientRequest = clientRequest.substring(0, 4)
                    println(clientRequest)

                    if (clientRequest == "JOIN") {
                        // Ajouter le client à la liste des clients connectés
                        if (!clients.contains(clientAddress)) {
                            clients[clientAddress] = clientPort
                            println("Nouveau client connecté : $clientAddress")
                            sendJoinResponse(clientAddress, clientPort, "${clients.size - 1}" + "JOIN_ACCEPTED")
                            val nomJoueur = nomJoueurDemande.toString().replace(" ", "")
                            println(game.getListeJoueurs().size)
                            val idJoueur = game.getListeJoueurs().size
                            val joueurAjoute = Joueur(
                                idJoueur,
                                nomJoueur
                            )
                            game.addPlayerToGame(
                                joueurAjoute
                            )
                            Platform.runLater{vueInGame.addPlayerToGrid(nomJoueur, 0, 0, idJoueur, 3, getImageViewList(joueurAjoute))}
                            println(nomJoueurDemande.toString().replace(" ", ""))

                        } else {
                            sendJoinResponse(clientAddress, clientPort, "0JOIN_DENIED")
                            println("denied")
                        }

                        // Envoyer les mises à jour du jeu au client
                        sendGameUpdate(clientAddress, clientPort, game)
                    } else if (game.getListeJoueurs().size == vueAccueil.getComboBoxValue()) {
                        if (game.etatJeu == EtatJeu.JEU_FINI) {
                            ControllerBoucleJeu(game, socket, joueur, vueAccueil, vueInGame, primaryStage).mainLoop()
                            thread?.interrupt()
                            thread = null

                        } else {
                            ControllerBoucleJeu(game, socket, joueur, vueAccueil, vueInGame, primaryStage).mainLoop()
                        }
                    }

                }


            }
            thread?.start()


        }


    }


    private fun sendGameUpdate(clientAddress: InetAddress, clientPort: Int, game: Game) {
        val socket = DatagramSocket()

        // Envoi de l'objet Game sérialisé au format JSON au client
        val json = serializeGameToJson(game)
        val sendData = json.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, clientAddress, clientPort)
        socket.send(sendPacket)

        socket.close()
    }

    private fun sendJoinResponse(clientAddress: InetAddress, clientPort: Int, response: String) {
        val socket = DatagramSocket()

        val sendData = response.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, clientAddress, clientPort)
        socket.send(sendPacket)

        socket.close()
    }

    private fun serializeGameToJson(game: Game): String {
        // Utilisez la bibliothèque de sérialisation JSON pour convertir l'objet Game en JSON
        // Exemple avec Gson :

        return Json.encodeToString(game)
    }

    fun getImageViewList(joueur: Joueur): MutableList<ImageView> {
        val cartesImageViewList: MutableList<ImageView> = joueur.main.map { carte ->
            val imageView = ImageView(carte.getImageView()) // Créez l'ImageView en utilisant l'image de la carte
            // Effectuez ici des configurations supplémentaires sur l'ImageView si nécessaire
            imageView
        }.toMutableList()
        return cartesImageViewList
    }


}