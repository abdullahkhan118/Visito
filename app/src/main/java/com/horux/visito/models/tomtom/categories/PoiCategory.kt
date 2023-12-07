package com.horux.visito.models.tomtom.categories

import com.google.gson.annotations.SerializedName

class PoiCategory {
    @SerializedName("id")
    var id: Long? = null

    @SerializedName("name")
    var name: String? = null
}
