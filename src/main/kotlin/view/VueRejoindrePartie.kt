package view

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox

class VueRejoindrePartie:  BorderPane() {
    private val centre = VBox()
    private val texteRejoindrePartie = Label("Rejoindre une partie")
    private val texteNomJoueurs = Label("Quel est votre pseudo ?")
    private val inputNomJoueurs = TextField("Pseudo")

    private val texteIdJoueur = Label("Quel est votre numéro (unique) de joueur")
    private val inputnombrejoueur = TextField("Numéro joueur")
    val boutonRejoindrePartie = Button("C'est parti !")
    init{
        centre.children.addAll(texteRejoindrePartie,texteNomJoueurs,inputNomJoueurs,texteIdJoueur,inputnombrejoueur,boutonRejoindrePartie)
        this.center = centre
    }



}