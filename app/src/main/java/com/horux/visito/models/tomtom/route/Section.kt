package com.horux.visito.models.tomtom.route

import com.google.gson.annotations.SerializedName

class Section {
    @SerializedName("endPointIndex")
    var endPointIndex: Long? = null

    @SerializedName("sectionType")
    var sectionType: String? = null

    @SerializedName("startPointIndex")
    var startPointIndex: Long? = null

    @SerializedName("travelMode")
    var travelMode: String? = null
}
