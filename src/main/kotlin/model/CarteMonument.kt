package model

import kotlinx.serialization.Serializable

@Serializable
class CarteMonument(private var nom:String,private var prix:Int,private var actif:Boolean) {

}