package com.horux.visito.models.tomtom.route

import com.google.gson.annotations.SerializedName

class Route {
    @SerializedName("legs")
    var legs: List<Leg>? = null

    @SerializedName("sections")
    var sections: List<Section>? = null

    @SerializedName("summary")
    var summary: Summary? = null
    override fun toString(): String {
        return "Route{" +
                "mLegs=" + legs +
                ", mSections=" + sections +
                ", mSummary=" + summary +
                '}'
    }
}
