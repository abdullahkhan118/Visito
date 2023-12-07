package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Result {
    @SerializedName("address")
    var address: Address? = null

    @SerializedName("dist")
    var dist: Double? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("info")
    var info: String? = null

    @SerializedName("poi")
    var poi: Poi? = null

    @SerializedName("position")
    var position: Position? = null

    @SerializedName("score")
    var score: Double? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("viewport")
    var viewport: Viewport? = null
}
