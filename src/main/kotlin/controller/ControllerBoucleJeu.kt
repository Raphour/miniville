package controller

import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.*
import view.InGame
import view.VueAccueil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class ControllerBoucleJeu(
    game: Game,
    socket: DatagramSocket,
    joueur: Joueur,
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
    private var joueur: Joueur

    init {
        this.game = game
        this.vueInGame = vueInGame
        this.primaryStage = primaryStage
        this.socket = socket
        this.vueAccueil = vueAccueil
        this.joueur = joueur

    }

    fun mainLoop() {
        println("BIENVENUE DANS MAINLOOP")
        try {

            val receivedGame = receivePacket()
            receivedGame.let {
                // Traitez l'objet Game selon vos besoins
                println("Game reçu : $it")

                // Stockez la liste de joueurs dans la variable joueurList
                var listeJoueurs = it.getListeJoueurs()
            }
        }catch(_:Exception){

        }




        if (game.getJoueurActuel() == joueur) {
            when (game.etatJeu) {
                EtatJeu.LANCER_DES -> {
                    game.lancerDes(1)

                    sendPacket(game, InetAddress.getLocalHost(), socket.port)
                }

                EtatJeu.APPLIQUER_OU_RELANCER_DES -> {
                    TODO("Implémenter la mécanique de choisir ou non grace à la Tour Radio, ou grace au parc d'attraction")
                }

                EtatJeu.VERIF_VICTOIRE -> {
                    if (game.checkWin() != null) {
                        game.etatJeu = EtatJeu.CHOISIR_NOMBRE_DES_ET_LANCER
                    }
                }

                EtatJeu.APPLIQUER_EFFETS -> {
                    var joueurVise: Joueur? = null
                    var carteDonnee: Carte? = null
                    var carteVoulue: Carte? = null
                    if (joueur.getId() == 0) {
                        if (game.getResultatLancer() == 6) {
                            fun removeItem(list: List<Joueur>, itemToRemove: Joueur): List<Joueur> {
                                return list.filterNot { it == itemToRemove }
                            }
                            if (joueur.main.contains(
                                    Carte(
                                        8,
                                        "Centre d'affaire",
                                        listOf(6),
                                        8,
                                        TypeBatiment.TOUR
                                    )
                                )
                            ) {
                                val comboBoxJoueurVise = ComboBox<String>()
                                val comboBoxCarteVoulue = ComboBox<String>()
                                val joueursVises = removeItem(game.getListeJoueurs(), joueur)
                                val listeNomJoueursVises = joueursVises.map { it.getNom() }
                                comboBoxJoueurVise.items.addAll(listeNomJoueursVises)
                                comboBoxJoueurVise.selectionModel.selectFirst()
                                comboBoxJoueurVise.setOnAction {
                                    val selectedPlayer: Joueur? =
                                        game.getListeJoueurs().find { comboBoxJoueurVise.value == it.getNom() }
                                    if (selectedPlayer != null) {
                                        comboBoxCarteVoulue.items =
                                            FXCollections.observableArrayList(selectedPlayer.main
                                                .filter { it.getType() != TypeBatiment.TOUR }
                                                .map { it.getNom() })
                                    }
                                }
                                comboBoxCarteVoulue.selectionModel.selectFirst()

                                val comboBoxCarteDonnee = ComboBox<String>()
                                comboBoxCarteDonnee.items =
                                    FXCollections.observableArrayList(joueur.main
                                        .filter { it.getType() != TypeBatiment.TOUR }
                                        .map { it.getNom() })
                                comboBoxCarteDonnee.selectionModel.selectFirst()

                                val gridPane = GridPane()
                                gridPane.hgap = 10.0
                                gridPane.vgap = 10.0
                                gridPane.addRow(0, comboBoxJoueurVise)
                                gridPane.addRow(1, comboBoxCarteVoulue)
                                gridPane.addRow(2, comboBoxCarteDonnee)

                                val dialog: Dialog<Void> = Dialog()
                                dialog.title = "Combo Box Dialog"
                                dialog.dialogPane.content = gridPane
                                dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)


                                // Handling user selection on OK button
                                dialog.setResultConverter { dialogButton ->
                                    if (dialogButton === ButtonType.OK) {
                                        joueurVise =
                                            game.getListeJoueurs().find { it.getNom() == comboBoxJoueurVise.value }
                                        carteDonnee =
                                            joueur.main.find { it.getNom() == comboBoxCarteDonnee.value }
                                        carteVoulue =
                                            joueurVise?.main?.find { it.getNom() == comboBoxCarteVoulue.value }
                                        // Do something with the selected values...
                                    }
                                    null
                                }


                            } else if (joueur.main.contains(
                                    Carte(
                                        9,
                                        "Chaîne de télévision",
                                        listOf(6),
                                        7,
                                        TypeBatiment.TOUR
                                    )
                                )
                            ) {
                                val comboBoxJoueurVise = ComboBox<String>()
                                val joueursVises = removeItem(game.getListeJoueurs(), joueur)
                                val listeNomJoueursVises = joueursVises.map { it.getNom() }
                                val choices: MutableList<String> = ArrayList()
                                choices.addAll(listeNomJoueursVises)


                                val dialog = ChoiceDialog(joueursVises[0].getNom(), choices)
                                comboBoxJoueurVise.selectionModel.selectFirst()
                                dialog.title = "Vous pouvez prendre 5 pièces à un joueur"
                                dialog.headerText = "Selectionnez le joueur à qui vous souhaitez prendre 5 pièces"
                                dialog.contentText = "Joueur :"

// Traditional way to get the response value.
                                val result = dialog.showAndWait()
                                val resultOrNull = result.orElse(null)
                                if (resultOrNull != null) {
                                    joueurVise = game.getListeJoueurs().find { it.getNom() == resultOrNull }
                                }
                                game.appliquerEffet(
                                    Carte(
                                        9,
                                        "Chaîne de télévision",
                                        listOf(6),
                                        7,
                                        TypeBatiment.TOUR
                                    ),
                                    game.getJoueurActuel()!!,
                                    game.getJoueurActuel()!!,
                                    joueurVise,
                                    carteVoulue,
                                    carteDonnee
                                )
                                sendPacket(game, InetAddress.getLocalHost(), socket.port)
                            }
                        } else
                            for (joueur in game.getListeJoueurs()) {
                                for (carte in joueur.main) {
                                    if (carte.getNumeros().contains(game.getResultatLancer())) {
                                        game.appliquerEffet(carte, game.getJoueurActuel()!!, joueur)
                                        sendPacket(game, InetAddress.getLocalHost(), socket.port)
                                    }
                                }
                            }
                        game.etatJeu = EtatJeu.ACHETER_OU_RIEN_FAIRE
                    }
                }


                EtatJeu.ATTENTE_JOUEURS -> {
                    if (vueAccueil.getComboBoxValue() != game.getListeJoueurs().size) {
                        game.etatJeu = EtatJeu.CHOISIR_NOMBRE_DES_ET_LANCER
                    }
                }

                EtatJeu.ACHETER_OU_RIEN_FAIRE -> {
                    if (game.getJoueurActuel()!!.getBourse() > game.reserve.minByOrNull { it.getPrix() }?.getPrix()!!) {
                        val alert = Alert(AlertType.CONFIRMATION)
                        alert.title = "Achat d'un batiment"
                        alert.headerText = "Souhaitez vous acheter un batiment "
                        alert.contentText = "Attention si vous choisissez OUI vous serez obligé d'acheter"

                        alert.buttonTypes.setAll(ButtonType.YES, ButtonType.NO)

                        val result = alert.showAndWait()
                        if (result.get() == ButtonType.NO) {
                            game.tourJoueurSuivant()
                            game.etatJeu = (EtatJeu.VERIF_VICTOIRE)
                        }
                    }

                }

                EtatJeu.CHOISIR_NOMBRE_DES_ET_LANCER -> {
                    if (joueur.getMainMonument()[0].getEtat()) {
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
                        game.etatJeu = (EtatJeu.APPLIQUER_EFFETS)
                        sendPacket(game, InetAddress.getLocalHost(), socket.port)

                    }
                }

                EtatJeu.JEU_FINI -> {
                    val alert = Alert(AlertType.INFORMATION)
                    alert.title = "Game Over"
                    alert.headerText = "La partie est finie"
                    alert.contentText = "${game.checkWin()?.getNom()} a réussi à construire ses monuments"

                    alert.showAndWait()
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




