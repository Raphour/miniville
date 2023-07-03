package controller

import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.EtatJeu
import model.Game
import view.InGame
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ControllerBoucleJeu(game: Game, port: Int, vueInGame: InGame, primaryStage: Stage) {
    private var game: Game
    private var vueInGame: InGame
    private var primaryStage: Stage
    private var port: Int
    private var bufferSize = 1024

    init {
        this.game = game
        this.vueInGame = vueInGame
        this.primaryStage = primaryStage
        this.port = port

    }

    fun mainLoop() {
        val receivedGame = receivePacket()

        receivedGame.let {
            // Traitez l'objet Game selon vos besoins
            println("Game reçu : $it")

            // Stockez la liste de joueurs dans la variable joueurList
            var listeJoueurs = it.getListeJoueurs()
        }

        if (game.getJoueurActuel() == game.getJoueur().getId()) {
            when (game.getEtat()) {
                EtatJeu.LANCER_DES -> {
                    if (game.getJoueur().getMainMonument()[0].getEtat()) {
                        TODO("Implementer le fait que le joueur peut choisir de lancer 1 ou 2 dés")
                    } else {
                        game.lancerDes(1)
                    }
                    sendPacket(game, InetAddress.getLocalHost(), port)
                }

                EtatJeu.APPLIQUER_OU_RELANCER_DES -> {
                    TODO("Implémenter la mécanique de choisir ou non grace à la Tour Radio, ou grace au parc d'attraction")
                }

                EtatJeu.APPLIQUER_EFFETS -> {
                    TODO("Implémenter la mécanique d'appliquer les effets donnés par les cartes")
                }

                EtatJeu.ATTENTE_JOUEURS -> {
                    TODO()
                }

                EtatJeu.ACHETER_OU_RIEN_FAIRE -> {
                    TODO("Implémenter le choix de construction du joueur ou ne rien faire")

                }

                EtatJeu.CHOISIR_NOMBRE_DES -> {
                    TODO("Afficher sur la vue un élément permettant au joueur de choisir le nombre de dé qu'il lance")
                }

                EtatJeu.JEU_FINI -> {
                    TODO("Implementer pop-up de fin de jeu")
                }
            }
            sendPacket(game,InetAddress.getLocalHost(),port)

        }

    }

    private fun receivePacket(): Game {
        val socket = DatagramSocket(5000) // Use the desired port number

        val buffer = ByteArray(1024) // Adjust the buffer size as per your requirements
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)

        val gameBytes = packet.data
        val game = deserializeGame(gameBytes.decodeToString()) // Deserialize the bytes back to a Game object

        socket.close()

        return game
    }

    private fun serializeGame(game: Game): String {
        return Json.encodeToString(game)
    }

    private fun deserializeGame(gameString: String): Game {
        return Json.decodeFromString(gameString)
    }

    private fun sendPacket(game: Game, ipAddress: InetAddress, port: Int) {
        val socket = DatagramSocket()

        val gameBytes = serializeGame(game).toByteArray() // Serialize the Game object to bytes

        val packet = DatagramPacket(gameBytes, gameBytes.size, ipAddress, port)
        socket.send(packet)

        socket.close()
    }

    fun updateVue(game: Game) {
        TODO("Mettre à jour la vuen en fonction des infromation des joueurs et du jeu")
    }

}




