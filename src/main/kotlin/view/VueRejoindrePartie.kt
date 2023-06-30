package view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
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
    private lateinit var  listeIdDispo : MutableList<Int>
    private var inputIdjoueur = ComboBox(FXCollections.observableArrayList(listeIdDispo))
    val boutonRejoindrePartie = Button("C'est parti !")
    init{
        centre.children.addAll(texteRejoindrePartie,texteNomJoueurs,inputNomJoueurs,texteIdJoueur,inputIdjoueur,boutonRejoindrePartie)
        this.center = centre
    }

    fun getIdField():Int {
        return this.inputIdjoueur.value.toInt()
    }

    fun getNomField():String{
        return this.inputNomJoueurs.text.replace(" ","")
    }

    fun setListeId(list:MutableList<Int>){
        this.listeIdDispo = list
    }





}