import controller.ControllerBoutonCreerPartie
import controller.ControllerBoutonRejoindrePartie
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import model.Game
import view.CreerPartie
import view.InGame
import view.VueAccueil
import view.VueRejoindrePartie
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*


class MonApplication : Application() {


    override fun start(primaryStage: Stage) {
        val game = Game()
        val vueInGame = InGame()
        val vueRejoindrePartie = VueRejoindrePartie()


        val vueAccueil = VueAccueil()
        vueAccueil.boutonRejoindrePartie.onAction = ControllerBoutonRejoindrePartie(game,vueRejoindrePartie, vueInGame, primaryStage)
        primaryStage.scene = Scene(vueAccueil,1920.0,1080.0)
        primaryStage.title = "Miniville"
        primaryStage.show()


    }

}





/**
 * Verifie si un port donné en argument n'est attribué à aucun service
 *
 * @param port
 * @return Un booléen décrivant la disponibilité du port
 */



