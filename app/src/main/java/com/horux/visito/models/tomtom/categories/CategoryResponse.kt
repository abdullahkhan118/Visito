package com.horux.visito.models.tomtom.categories

import com.google.gson.annotations.SerializedName

class CategoryResponse {
    @SerializedName("poiCategories")
    var poiCategories: List<PoiCategory>? = null
}
