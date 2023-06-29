import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import view.InGame
import model.Game
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*


class MonApplication: Application() {


    override fun start(primaryStage: Stage?) {
        val vue = InGame()
        val model = Game(2)
    }

}
// Port d'écoute pour les paquets
val port = generateUniquePort(5000,6000)
// Taille maximale du buffer pour les paquets reçus
const val bufferSize = 1024
fun partieExistante(): Boolean {

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

// Créer une nouvelle partie
fun creerPartie(port:Int) {
    val socket = DatagramSocket()
    val message = "Nouvelle partie".toByteArray()
    val packet = DatagramPacket(message, message.size, InetAddress.getLocalHost(), port)

    // Envoyer le paquet
    socket.send(packet)
    socket.close()

    println("Nouvelle partie créée !")
}

// Vérifier s'il faut créer ou rejoindre une partie
fun verifierPartie() {
    if (partieExistante()) {
        // Une partie existe déjà, rejoindre la partie
        println("Rejoindre une partie existante...")
    } else {
        // Aucune partie existante, créer une nouvelle partie
        creerPartie(port)
    }
}


/**
 * Verifie si un port donné en argument n'est attribué à aucun service
 *
 * @param port
 * @return Un booléen décrivant la disponibilité du port
 */
fun isPortAvailable(port: Int): Boolean {
    return try {
        ServerSocket(port).use { socket ->
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
fun generateUniquePort(minPort: Int, maxPort: Int): Int {
    val random = Random()
    var port = random.nextInt(maxPort - minPort + 1) + minPort
    while (!isPortAvailable(port)) {
        port = random.nextInt(maxPort - minPort + 1) + minPort
    }
    return port
}


