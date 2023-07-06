import controller.ControllerBoutonCreerPartie
import controller.ControllerBoutonRejoindrePartie
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import model.Game
import view.InGame
import view.VueAccueil
import view.VueCreerPartie
import view.VueRejoindrePartie
import java.util.*

fun main() {
    Application.launch(Main::class.java)

}
class Main : Application() {




    override fun start(primaryStage: Stage) {
        val game = Game()
        val vueInGame = InGame()
        val vueRejoindrePartie = VueRejoindrePartie()
        val vueCreerPartie = VueCreerPartie()


        val vueAccueil = VueAccueil()
        vueAccueil.boutonCreerPartie.onAction = ControllerBoutonCreerPartie(game,vueAccueil,vueInGame, primaryStage)
        vueAccueil.boutonRejoindrePartie.onAction = ControllerBoutonRejoindrePartie(game,vueRejoindrePartie,vueInGame,vueAccueil,primaryStage)
        vueAccueil.boutonRejoindrePartie.onAction = ControllerBoutonRejoindrePartie(game,vueRejoindrePartie, vueInGame, vueAccueil,primaryStage)
        primaryStage.scene = Scene(vueAccueil,1650.0,1110.0)
        primaryStage.title = "Miniville"
        primaryStage.show()


    }



}





