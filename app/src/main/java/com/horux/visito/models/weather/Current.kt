package com.horux.visito.models.weather

import com.google.gson.annotations.SerializedName

class Current {
    @SerializedName("cloud")
    var cloud: Long? = null

    @SerializedName("condition")
    var condition: Condition? = null

    @SerializedName("feelslike_c")
    var feelslikeC: Double? = null

    @SerializedName("feelslike_f")
    var feelslikeF: Double? = null

    @SerializedName("gust_kph")
    var gustKph: Double? = null

    @SerializedName("gust_mph")
    var gustMph: Double? = null

    @SerializedName("humidity")
    var humidity: Long? = null

    @SerializedName("is_day")
    var isDay: Long? = null

    @SerializedName("last_updated")
    var lastUpdated: String? = null

    @SerializedName("last_updated_epoch")
    var lastUpdatedEpoch: Long? = null

    @SerializedName("precip_in")
    var precipIn: Double? = null

    @SerializedName("precip_mm")
    var precipMm: Double? = null

    @SerializedName("pressure_in")
    var pressureIn: Double? = null

    @SerializedName("pressure_mb")
    var pressureMb: Double? = null

    @SerializedName("temp_c")
    var tempC: Double? = null

    @SerializedName("temp_f")
    var tempF: Double? = null

    @SerializedName("uv")
    var uv: Double? = null

    @SerializedName("vis_km")
    var visKm: Double? = null

    @SerializedName("vis_miles")
    var visMiles: Double? = null

    @SerializedName("wind_degree")
    var windDegree: Long? = null

    @SerializedName("wind_dir")
    var windDir: String? = null

    @SerializedName("wind_kph")
    var windKph: Double? = null

    @SerializedName("wind_mph")
    var windMph: Double? = null
}
