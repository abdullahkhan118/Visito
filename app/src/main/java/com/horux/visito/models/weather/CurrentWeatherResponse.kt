package com.horux.visito.models.weather

import com.google.gson.annotations.SerializedName

class CurrentWeatherResponse {
    @SerializedName("current")
    var current: Current? = null

    @SerializedName("location")
    var location: Location? = null
}
