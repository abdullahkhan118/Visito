package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Poi {
    @SerializedName("categories")
    var categories: List<String?>? = null

    @SerializedName("categorySet")
    var categorySet: List<CategorySet>? = null

    @SerializedName("classifications")
    var classifications: List<Classification>? = null

    @SerializedName("name")
    var name: String? = null
}
