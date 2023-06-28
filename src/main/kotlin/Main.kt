import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import view.InGame
import model.Game

class MonApplication: Application() {
    override fun start(primaryStage: Stage?) {
        val vue = InGame()
        val model = Game(2)
    }

}