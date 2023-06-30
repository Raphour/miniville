package model

import kotlinx.serialization.Serializable

@Serializable
class CarteMonument(private var nom:String,private var prix:Int,private var actif:Boolean) {
    fun getPrix():Int{
        return prix
    }

    fun getNom():String{
        return nom
    }
    fun getEtat():Boolean{
        return actif
    }

    fun setEtat(etat:Boolean){
        this.actif = etat
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CarteMonument

        if (nom != other.nom) return false
        if (prix != other.prix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nom.hashCode()
        result = 31 * result + prix
        return result
    }

    override fun toString(): String {
        return "CarteMonument(nom='$nom', actif=$actif)"
    }

}