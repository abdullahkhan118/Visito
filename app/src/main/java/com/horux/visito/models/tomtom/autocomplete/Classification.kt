package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Classification {
    @SerializedName("code")
    var code: String? = null

    @SerializedName("names")
    var names: List<Name>? = null
}
