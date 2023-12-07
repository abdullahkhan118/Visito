package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Summary {
    @SerializedName("fuzzyLevel")
    var fuzzyLevel: Long? = null

    @SerializedName("geoBias")
    var geoBias: GeoBias? = null

    @SerializedName("numResults")
    var numResults: Long? = null

    @SerializedName("offset")
    var offset: Long? = null

    @SerializedName("query")
    var query: String? = null

    @SerializedName("queryTime")
    var queryTime: Long? = null

    @SerializedName("queryType")
    var queryType: String? = null

    @SerializedName("totalResults")
    var totalResults: Long? = null
}
