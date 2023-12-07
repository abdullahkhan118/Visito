package com.horux.visito.models.tomtom.route

import com.google.gson.annotations.SerializedName

class Summary {
    @SerializedName("arrivalTime")
    var arrivalTime: String? = null

    @SerializedName("departureTime")
    var departureTime: String? = null

    @SerializedName("lengthInMeters")
    var lengthInMeters: Long? = null

    @SerializedName("trafficDelayInSeconds")
    var trafficDelayInSeconds: Long? = null

    @SerializedName("trafficLengthInMeters")
    var trafficLengthInMeters: Long? = null

    @SerializedName("travelTimeInSeconds")
    var travelTimeInSeconds: Long? = null
}
