package model

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.io.FileInputStream





@Serializable
class Carte(private var id:Int,private var nom:String,private var numeros:List<Int>,private var prix:Int,private var type:TypeBatiment):Comparable<Carte> {


    @Contextual
    private lateinit var imageView: ImageView
    private lateinit var couleur : String

    init {
        setCouleur()
        setImageView()
    }

    private fun setImageView(){
        val input = FileInputStream("assets/carte${nom}.png")
        val image: Image = Image(input)
        this.imageView = ImageView(image)

    }

    private fun setCouleur(){
        when(type){
            TypeBatiment.CAFE -> this.couleur = "rouge"
            TypeBatiment.ENGRENAGE,TypeBatiment.EPI,TypeBatiment.VACHE -> this.couleur = "bleu"
            TypeBatiment.MAISON,TypeBatiment.USINE,TypeBatiment.FRUIT -> this.couleur = "vert"
            TypeBatiment.TOUR -> this.couleur = "violet"
            else -> {}
        }
    }

    //GETTER

    fun getNom():String{return this.nom}
    fun getCouleur():String{return this.couleur}
    fun getNumeros():List<Int>{return this.numeros}
    fun getImageView():String{return this.nom}
    fun getPrix():Int{return this.prix}
    fun getType():TypeBatiment{
        return this.type
    }

    override fun compareTo(other: Carte): Int {
        return this.numeros[0].compareTo(other.numeros[0])
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Carte

        if (nom != other.nom) return false

        return true
    }


    override fun hashCode(): Int {
        return nom.hashCode()
    }

    override fun toString(): String {
        return "Carte(id=$id, nom='$nom')"
    }

    fun getTextEffect
}