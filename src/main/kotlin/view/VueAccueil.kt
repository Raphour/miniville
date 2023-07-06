package view

import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class VueAccueil : VBox() {
    private val texteFieldNomJoueur = TextField()
    val boutonCreerPartie = Button("Créer une partie")
    private val comboBoxNbPlayer = ComboBox(FXCollections.observableArrayList(listOf(2, 3, 4)))

    private val hboxCreationPartie = HBox()
    val boutonRejoindrePartie = Button("Rejoindre une partie")

    init {
        hboxCreationPartie.children.addAll(boutonCreerPartie, comboBoxNbPlayer)
        comboBoxNbPlayer.selectionModel.selectFirst()
        this.children.addAll(texteFieldNomJoueur,hboxCreationPartie, boutonRejoindrePartie)
    }

    fun getComboBoxValue(): Int {
        return this.comboBoxNbPlayer.value
    }

    fun getNomJoueur():String{
        return texteFieldNomJoueur.text.replace(" ","")
    }
}


