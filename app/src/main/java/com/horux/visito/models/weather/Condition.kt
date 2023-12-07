package com.horux.visito.models.weather

import com.google.gson.annotations.SerializedName

class Condition {
    @SerializedName("code")
    var code: Long? = null

    @SerializedName("icon")
    var icon: String? = null

    @SerializedName("text")
    var text: String? = null
}
