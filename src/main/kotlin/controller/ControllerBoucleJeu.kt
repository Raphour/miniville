package controller

import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.CarteMonument
import model.EtatJeu
import model.Game
import model.Joueur
import view.InGame
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ControllerBoucleJeu(game: Game,port:Int, vueInGame: InGame, primaryStage: Stage) {
    private var game: Game
    private var vueInGame: InGame
    private var primaryStage: Stage
    private var port : Int
    private var bufferSize = 1024

    init {
        this.game = game
        this.vueInGame = vueInGame
        this.primaryStage = primaryStage
        this.port = port

    }

    fun mainLoop(){

        var lastPacket: DatagramPacket? = null
        val buffer = ByteArray(bufferSize)
        val packet = DatagramPacket(buffer, buffer.size)
        val socket = DatagramSocket(port)
        val listeJoueurs : MutableList<Joueur>?

        socket.receive(packet) // Réception du paquet

        lastPacket = packet // Stockage du dernier paquet reçu

        // Traitez le paquet selon vos besoins
        val receivedData = String(packet.data, 0, packet.length)
        println("Paquet reçu : $receivedData")
        // Désérialisation des données du paquet en tant qu'objet Game
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
        }
        if(game.getJoueurActuel() == game.getJoueur().getId() && game.getEtat() == EtatJeu.LANCER_DES){
            if(game.getJoueur().getMainMonument()[0].getEtat()){
                TODO("Implementer le fait que le joueur peut choisir de lancer 1 ou 2 dés")
            }else{
                game.lancerDes(1)


            }
            sendPacket(game,InetAddress.getLocalHost(),port)

        }


    }
}

fun sendPacket(game: Game, ipAddress: InetAddress, port: Int) {
    val socket = DatagramSocket()

    val gameBytes = serializeGame(game).toByteArray() // Serialize the Game object to bytes

    val packet = DatagramPacket(gameBytes, gameBytes.size, ipAddress, port)
    socket.send(packet)

    socket.close()
}

fun receivePacket(): Game {
    val socket = DatagramSocket(5000) // Use the desired port number

    val buffer = ByteArray(1024) // Adjust the buffer size as per your requirements
    val packet = DatagramPacket(buffer, buffer.size)
    socket.receive(packet)

    val gameBytes = packet.data
    val game = deserializeGame(gameBytes.decodeToString()) // Deserialize the bytes back to a Game object

    socket.close()

    return game
}
fun serializeGame(game: Game): String {
    return Json.encodeToString(game)
}

fun deserializeGame(gameString: String): Game {
    return Json.decodeFromString(gameString)
}
