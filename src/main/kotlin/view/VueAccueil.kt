package view

import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.layout.VBox

class VueAccueil : VBox() {
    val boutonCreerPartie = Button("Créer une partie")
    private val comboBoxNbPlayer = ComboBox(FXCollections.observableArrayList(listOf(2, 3, 4)))
    private val vboxCreationPartie = VBox()
    val boutonRejoindrePartie = Button("Rejoindre une partie")

    init {
        vboxCreationPartie.children.addAll(boutonCreerPartie, comboBoxNbPlayer)
        this.children.addAll(boutonCreerPartie, boutonRejoindrePartie)
    }

    fun getComboBoxValue(): Int {
        return this.comboBoxNbPlayer.value
    }
}


