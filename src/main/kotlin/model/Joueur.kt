package model


import kotlinx.serialization.Serializable

@Serializable
class Joueur(private var id:Int, private var nom:String) {

    private var bourse : Int
    var main : MutableList<Carte>
    private var mainMonument : MutableList<CarteMonument>

    init {
        this.bourse = 3
        this.main = mutableListOf()
        this.mainMonument = mutableListOf()
        this.remplirMainMonuments()
    }

    fun getId():Int{
        return this.id
    }

    fun getNom():String{
        return this.nom
    }

    /**
     * Retourne la valeur de la bourse du joueur
     *
     * @return Int
     */
    fun getBourse():Int{
        return this.bourse
    }

    fun getMainMonument():MutableList<CarteMonument>{
        return this.mainMonument
    }



    private fun remplirMainMonuments(){
        this.mainMonument.add(CarteMonument("Gare",4,false))
        this.mainMonument.add(CarteMonument("Centre commercial",1,false))
        this.mainMonument.add(CarteMonument("Parc d'attraction",16,false))
        this.mainMonument.add(CarteMonument("Tour radio",22,false))
    }

    fun addBourse(montant:Int){
        this.bourse += montant
    }

    /**
     * Enleve le montant donné en paramètre au joueur, si la somme ne le permet pas alors le maximum est retiré
     *
     * @param montant
     * @return Int
     */
    fun removeBourse(montant:Int):Int{
        if(this.bourse>montant) {
            this.bourse -= montant
            return montant
        }
        val sommRetiree = this.bourse
        this.bourse = 0
        return sommRetiree

    }



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Joueur

        if (id != other.id) return false
        if (nom != other.nom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nom.hashCode()
        return result
    }

    override fun toString(): String {
        return "Joueur(id=$id, nom='$nom', bourse=$bourse, main=$main, mainMonument=$mainMonument)"
    }
}