package fr.isen.etavard.androiderestaurant.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Ingredient(@SerializedName("name_fr") val name: String): Serializable