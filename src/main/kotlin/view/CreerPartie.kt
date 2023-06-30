package view


import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox


class CreerPartie : BorderPane() {
    private val centre = VBox()
    private val texteCreerPartie = Label("Créer une partie")
    private val texteNombreJoueurs = Label("Nombre de joueurs")
    private val inputNombreJoueurs = TextField("Nombre joueurs")
    val boutonCreerPartie = Button("C'est parti !")
    init{
        centre.children.addAll(texteCreerPartie,texteNombreJoueurs,inputNombreJoueurs,boutonCreerPartie)
        this.center = centre
    }


}