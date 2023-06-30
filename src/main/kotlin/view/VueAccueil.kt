package view

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.TextField
import javafx.scene.layout.VBox

class VueAccueil: VBox() {
    val boutonCreerPartie = Button("Créer une partie")
    val boutonRejoindrePartie = Button("Rejoindre une partie")

    init {
        this.children.addAll(boutonCreerPartie,boutonRejoindrePartie)
    }
}


