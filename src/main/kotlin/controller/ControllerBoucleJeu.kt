package controller

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.EtatJeu
import model.Game
import view.InGame
import view.VueAccueil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class ControllerBoucleJeu(
    game: Game,
    socket: DatagramSocket,
    vueAccueil: VueAccueil,
    vueInGame: InGame,
    primaryStage: Stage
) {
    private var game: Game
    private var vueInGame: InGame
    private var primaryStage: Stage
    private var socket: DatagramSocket
    private var bufferSize = 1024
    private var vueAccueil: VueAccueil

    init {
        this.game = game
        this.vueInGame = vueInGame
        this.primaryStage = primaryStage
        this.socket = socket
        this.vueAccueil = vueAccueil

    }

    fun mainLoop() {
        println("BIENVENUE DANS MAINLOOP")
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
                    game.lancerDes(1)

                    sendPacket(game, InetAddress.getLocalHost(), socket.port)
                }

                EtatJeu.APPLIQUER_OU_RELANCER_DES -> {
                    TODO("Implémenter la mécanique de choisir ou non grace à la Tour Radio, ou grace au parc d'attraction")
                }

                EtatJeu.APPLIQUER_EFFETS -> {
                }

                EtatJeu.ATTENTE_JOUEURS -> {
                    if (vueAccueil.getComboBoxValue() != game.getListeJoueurs().size) {
                        game.setEtat(EtatJeu.CHOISIR_NOMBRE_DES_ET_LANCER)
                    }
                }

                EtatJeu.ACHETER_OU_RIEN_FAIRE -> {
                    TODO("Implémenter le choix de construction du joueur ou ne rien faire")

                }

                EtatJeu.CHOISIR_NOMBRE_DES_ET_LANCER -> {
                    if (game.getJoueur().getMainMonument()[0].getEtat()) {
                        val alert = Alert(AlertType.CONFIRMATION)
                        alert.title = "C'est votre tour"
                        alert.headerText = "Vous pouvez choisir le nombre de dés à lancer"
                        alert.contentText = "Choisissez une option"

                        val buttonTypeOne = ButtonType("1")
                        val buttonTypeTwo = ButtonType("2")

                        alert.buttonTypes.setAll(buttonTypeOne, buttonTypeTwo)

                        val result = alert.showAndWait()
                        if (result.get() == buttonTypeOne) {
                            game.lancerDes(1)
                        } else if (result.get() == buttonTypeTwo) {
                            game.lancerDes(2)
                        }
                        game.setEtat(EtatJeu.APPLIQUER_EFFETS)

                    }
                }

                EtatJeu.JEU_FINI -> {
                    TODO("Implementer pop-up de fin de jeu")
                }
            }
            sendPacket(game, InetAddress.getLocalHost(), socket.port)

        }

    }

    private fun receivePacket(): Game {


        val buffer = ByteArray(1024) // Adjust the buffer size as per your requirements
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)

        val gameBytes = packet.data


        return deserializeGame(String(gameBytes, 0, packet.length))
    }

    private fun serializeGame(game: Game): String {
        return Json.encodeToString(game)
    }

    private fun deserializeGame(gameString: String): Game {
        println(gameString)
        return Json.decodeFromString<Game>(gameString)
    }

    private fun sendPacket(game: Game, ipAddress: InetAddress, port: Int) {
        val socket = DatagramSocket(0)

        val gameBytes = serializeGame(game).toByteArray() // Serialize the Game object to bytes

        val packet = DatagramPacket(gameBytes, gameBytes.size, ipAddress, socket.localPort)
        socket.send(packet)

        socket.close()
    }

    fun updateVue(game: Game) {
        TODO("Mettre à jour la vuen en fonction des infromation des joueurs et du jeu")
    }

}




