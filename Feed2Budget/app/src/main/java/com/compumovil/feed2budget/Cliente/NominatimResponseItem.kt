package com.compumovil.feed2budget.Cliente

import com.google.gson.annotations.SerializedName

data class NominatimResponseItem(
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String
)
