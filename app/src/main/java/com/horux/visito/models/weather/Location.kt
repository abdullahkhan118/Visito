package com.horux.visito.models.weather

import com.google.gson.annotations.SerializedName

class Location {
    @SerializedName("country")
    var country: String? = null

    @SerializedName("lat")
    var lat: Double? = null

    @SerializedName("localtime")
    var localtime: String? = null

    @SerializedName("localtime_epoch")
    var localtimeEpoch: Long? = null

    @SerializedName("lon")
    var lon: Double? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("region")
    var region: String? = null

    @SerializedName("tz_id")
    var tzId: String? = null
}
