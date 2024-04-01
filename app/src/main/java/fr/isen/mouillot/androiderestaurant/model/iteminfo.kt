package fr.isen.mouillot.androiderestaurant.model

import com.google.gson.annotations.SerializedName

data class iteminfo(
    @SerializedName("item_Name") var itemName: String = "",
    @SerializedName("item_Price") var itemPrice: Int = 0,
    @SerializedName("quantity") var quantity: Int = 0
)