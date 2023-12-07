package com.horux.visito.models.tomtom.route

import com.google.gson.annotations.SerializedName

class Point {
    @SerializedName("latitude")
    var latitude: Double? = null

    @SerializedName("longitude")
    var longitude: Double? = null
}
