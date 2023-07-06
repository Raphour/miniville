package model


import kotlinx.serialization.Serializable

@Serializable
class Game {

    var reserve: MutableList<Carte> = mutableListOf()
    private var lanceDes: Pair<Int, Int?> = Pair<Int, Int?>(0, null)
    var etatJeu: EtatJeu = EtatJeu.ATTENTE_JOUEURS
        get() = field
        set(value) {
            field = value
        }
    private lateinit var joueurActuel: Joueur
    private var nbDesLances: Int = 0

    private var listeJoueurs: MutableList<Joueur> = mutableListOf()

    private lateinit var player: Joueur
    init {
        setActualPlayerById(0)
    }

    fun preremplirCarte() {
        for (i in 0 until 4) {
            this.reserve.add(Carte(1, "Champs de blé", listOf(1), 1, TypeBatiment.EPI))
            this.reserve.add(Carte(2, "Ferme", listOf(2), 1, TypeBatiment.VACHE))
            this.reserve.add(Carte(3, "Boulangerie", listOf(2, 3), 1, TypeBatiment.MAISON))
            this.reserve.add(Carte(4, "Café", listOf(3), 2, TypeBatiment.CAFE))
            this.reserve.add(Carte(5, "Supérette", listOf(4), 2, TypeBatiment.MAISON))
            this.reserve.add(Carte(6, "Forêt", listOf(5), 3, TypeBatiment.ENGRENAGE))
            this.reserve.add(Carte(7, "Stade", listOf(6), 6, TypeBatiment.TOUR))
            this.reserve.add(Carte(8, "Centre d'affaire", listOf(6), 8, TypeBatiment.TOUR))
            this.reserve.add(Carte(9, "Chaîne de télévision", listOf(6), 7, TypeBatiment.TOUR))
            this.reserve.add(Carte(10, "Fromagerie", listOf(7), 5, TypeBatiment.USINE))
            this.reserve.add(Carte(11, "Fabrique meuble", listOf(8), 3, TypeBatiment.USINE))
            this.reserve.add(Carte(12, "Mine", listOf(9), 6, TypeBatiment.ENGRENAGE))
            this.reserve.add(Carte(13, "Restaurant", listOf(9, 10), 3, TypeBatiment.CAFE))
            this.reserve.add(Carte(14, "Verger", listOf(10), 3, TypeBatiment.EPI))
            this.reserve.add(Carte(15, "Marché fruits et légumes", listOf(11, 12), 2, TypeBatiment.FRUIT))


        }
    }

    fun lancerDes(nbDes: Int) {
        if (etatJeu == EtatJeu.LANCER_DES) {

            val de1 = (1..6).random()
            var de2: Int? = null
            if (nbDes == 2) {
                de2 = (1..6).random()
            }
            this.lanceDes = Pair(de1, de2)
            this.etatJeu = EtatJeu.APPLIQUER_OU_RELANCER_DES

        } else {
            throw Exception("Ce n'est pas le moment de lancer les dés")
        }

    }

    fun setPlayer(joueur: Joueur) {
        this.player = joueur
    }

    fun tourJoueurSuivant(){
        if (getActualPlayerId()==listeJoueurs.size){
            setActualPlayerById(0)
        }else{
            setActualPlayerById(getActualPlayerId().plus(1)  )
        }
    }

    fun getNbDesLances(): Int {
        return this.nbDesLances
    }

    fun setNbDesLances(nb: Int) {
        this.nbDesLances = nb
    }

    fun getJoueurActuel(): Joueur? {
        return joueurActuel
    }
    fun getActualPlayerId():Int{
        return joueurActuel.getId()
    }

    fun setActualPlayerById(id:Int){
        val joueurActuelOrNull = listeJoueurs.find { it.getId() == id }
        if (joueurActuelOrNull != null){
            this.joueurActuel = joueurActuelOrNull
        }
    }

    fun getJoueur(): Joueur {
        return player
    }

    fun addPlayerToGame(joueur: Joueur) {
        this.listeJoueurs.add(joueur)
    }

    fun getEtat(): EtatJeu {
        return this.etatJeu
    }

    fun setEtat(etatJeu: EtatJeu) {
        this.etatJeu = etatJeu
    }

    fun getListeJoueurs(): MutableList<Joueur> {
        return this.listeJoueurs
    }

    fun getResultatLancer(): Int {
        var resultat = this.lanceDes.first
        if (this.lanceDes.second != null) {
            resultat += this.lanceDes.second!!
        }
        return resultat
    }

    fun checkWin():Joueur?{
        for(joueur in listeJoueurs){
            if (joueur.getMainMonument().count { it.getEtat() } == 4){
                etatJeu = EtatJeu.JEU_FINI
                return joueur
            }
        }
        return null
    }


    fun construireBatiment(batiment: Carte, joueur: Joueur) {
        if (etatJeu == EtatJeu.ACHETER_OU_RIEN_FAIRE) {
            if (joueur.getBourse() >= batiment.getPrix()) {
                joueur.main.add(batiment)
                this.reserve.remove(batiment)
                joueur.removeBourse(batiment.getPrix())
            }
        } else {
            throw Exception("Ce n'est pas le moment de construire")
        }
    }

    fun construireMonument(monument: CarteMonument, joueur: Joueur) {
        if (etatJeu == EtatJeu.ACHETER_OU_RIEN_FAIRE) {
            if (joueur.getBourse() >= monument.getPrix() && !monument.getEtat()) {
                joueur.removeBourse(monument.getPrix())
                for (m in joueur.getMainMonument()) {
                    if (m == monument) {
                        m.setEtat(true)
                    }
                }
            }
        } else {
            throw Exception("Ce n'est pas le moment de construire")
        }
    }

    fun distributionCarteJoueur() {
        for (joueurs in listeJoueurs) {
            joueurs.main.add(Carte(1, "Champs de blé", listOf(1), 1, TypeBatiment.EPI))
            joueurs.main.add(Carte(2, "Ferme", listOf(2), 1, TypeBatiment.VACHE))
        }
    }

    fun appliquerEffet(
        carte: Carte,
        joueurActuel: Joueur,
        joueurAppliquerEffet: Joueur,
        joueurVise: Joueur? = null,
        carteVoulue: Carte? = null,
        carteDonnee: Carte? = null
    ) {
        when (carte.getNom()) {
            "Champs de blé" -> {
                val compteChampsBle = joueurAppliquerEffet.main.count { it.getNom() == "Champs de Blé" }
                joueurAppliquerEffet.addBourse(compteChampsBle)

            }

            "Ferme" -> {
                val compteFerme = joueurAppliquerEffet.main.count { it.getNom() == "Ferme" }
                joueurAppliquerEffet.addBourse(compteFerme)

            }

            "Boulangerie" -> {
                if (joueurActuel == joueurAppliquerEffet) {
                    val compteBoulangerie = joueurAppliquerEffet.main.count { it.getNom() == "Boulangerie" }
                    joueurAppliquerEffet.addBourse(compteBoulangerie)
                }

            }

            "Café" -> {
                for (joueur in listeJoueurs) {
                    if (joueur != joueurAppliquerEffet) {
                        val compteCafe = joueur.main.count { it.getNom() == "Café" }
                        joueur.addBourse(joueurActuel.removeBourse(compteCafe * 3))
                    }
                }
            }

            "Supérette" -> {
                if (joueurActuel == joueurAppliquerEffet) {
                    val compteSuperette = joueurAppliquerEffet.main.count { it.getNom() == "Supérette" }
                    joueurAppliquerEffet.addBourse(compteSuperette * 3)
                }

            }

            "Forêt" -> {
                val compteForet = joueurAppliquerEffet.main.count { it.getNom() == "Forêt" }
                joueurAppliquerEffet.addBourse(compteForet * 3)
            }

            "Stade" -> {
                if (joueurActuel == joueurAppliquerEffet) {
                    for (joueur in listeJoueurs) {
                        joueurActuel.addBourse(joueur.removeBourse(2))
                    }
                }
            }

            "Centre d'affaire" -> {
                if (joueurActuel == joueurAppliquerEffet) {
                    if (joueurVise != null && carteVoulue != null && carteDonnee != null) {

                        val indexCarteVoulueDansMain =
                            joueurVise.main.indexOfFirst { it.getNom() == carteVoulue.getNom() }
                        val indexCarteDonneDansMain =
                            joueurActuel.main.indexOfFirst { it.getNom() == carteDonnee.getNom() }
                        if (indexCarteVoulueDansMain != -1) {
                            if (indexCarteDonneDansMain != -1) {
                                joueurActuel.main.add(carteVoulue)
                                joueurVise.main.add(carteDonnee)
                                joueurActuel.main.remove(carteDonnee)
                                joueurVise.main.remove(carteVoulue)

                            } else {
                                throw Exception("Le joueur ne possède pas la carte qu'il veut échanger")
                            }
                        } else {
                            throw Exception("Carte non présente dans la main de l'adversaire")
                        }
                    }
                }

            }

            "Chaîne de télévision" -> {
                if (joueurActuel == joueurAppliquerEffet) {
                    if (joueurVise != null) {
                        joueurActuel.addBourse(joueurVise.removeBourse(5))
                    }
                }

            }

            "Fromagerie" -> {
                if (joueurAppliquerEffet == joueurActuel) {
                    for (cartejoueur in joueurActuel.main) {
                        if (cartejoueur.getType() == TypeBatiment.VACHE) {
                            joueurActuel.addBourse(3)
                        }
                    }
                }

            }

            "Fabrique meuble" ->
                if (joueurAppliquerEffet == joueurActuel) {
                    for (cartejoueur in joueurActuel.main) {
                        if (cartejoueur.getType() == TypeBatiment.ENGRENAGE) {
                            joueurActuel.addBourse(3)
                        }
                    }
                }

            "Mine" -> {


                val compteMine = joueurAppliquerEffet.main.count { it.getNom() == "Mine" }
                joueurAppliquerEffet.addBourse(compteMine * 5)

            }

            "Restaurant" -> {
                if (joueurActuel == joueurAppliquerEffet) {

                    for (joueur in listeJoueurs) {
                        if (joueur != joueurAppliquerEffet) {
                            val compteRestaurant = joueur.main.count { it.getNom() == "Restaurant" }
                            joueur.addBourse(joueurActuel.removeBourse(compteRestaurant * 2))
                        }
                    }
                }
            }

            "Verger" -> {
                val compteVerger = joueurAppliquerEffet.main.count { it.getNom() == "Verger" }
                joueurAppliquerEffet.addBourse(3 * compteVerger)
            }


            "Marché fruits et légumes" -> {
                if (joueurActuel == joueurAppliquerEffet) {
                    val compteEpi = joueurAppliquerEffet.main.count { it.getType() == TypeBatiment.EPI }
                    joueurAppliquerEffet.addBourse(2 * compteEpi)
                }
            }

        }


    }


    override fun toString(): String {
        return "Game(reserve=$reserve, lanceDes=$lanceDes, etatJeu=$etatJeu, joueurActuel=$joueurActuel, listeJoueurs=$listeJoueurs, player=$player)"
    }
}