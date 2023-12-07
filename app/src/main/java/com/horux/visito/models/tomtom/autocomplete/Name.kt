package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Name {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("nameLocale")
    var nameLocale: String? = null
}
