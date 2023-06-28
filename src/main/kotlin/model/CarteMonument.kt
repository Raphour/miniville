package model

class CarteMonument(nom:String,prix:Int,actif:Boolean) {
    private var nom : String
    private var prix: Int
    private var actif:Boolean
    init {
        this.nom = nom
        this.actif = actif
        this.prix = prix
    }
}