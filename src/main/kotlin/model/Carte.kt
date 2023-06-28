package model

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.FileInputStream






class Carte(id:Int,nom:String,numeros:List<Int>,prix:Int,type:TypeBatiment):Comparable<Carte> {
    private var nom : String
    private var prix : Int
    private var type : TypeBatiment
    private var id : Int

    private lateinit var imageView: ImageView
    private var numeros : List<Int>
    private lateinit var couleur : String

    init {
        this.nom = nom
        this.prix = prix
        this.type = type
        this.numeros = numeros
        this.id = id
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
}