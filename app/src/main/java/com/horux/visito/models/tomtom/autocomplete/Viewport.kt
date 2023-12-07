package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Viewport {
    @SerializedName("btmRightPoint")
    var btmRightPoint: BtmRightPoint? = null

    @SerializedName("topLeftPoint")
    var topLeftPoint: TopLeftPoint? = null
}
