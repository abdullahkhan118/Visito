package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Position {
    @SerializedName("lat")
    var lat: Double? = null

    @SerializedName("lon")
    var lon: Double? = null
}
