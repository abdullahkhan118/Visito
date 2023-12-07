package com.horux.visito.models.tomtom.route

import com.google.gson.annotations.SerializedName

class Leg {
    @SerializedName("points")
    var points: List<Point>? = null

    @SerializedName("summary")
    var summary: Summary? = null
}
