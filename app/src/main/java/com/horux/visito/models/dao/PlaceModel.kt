package com.horux.visito.models.dao

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

data class PlaceModel(
    var id: String? = null,
    var image: String? = null,
    var title: String? = null,
    var description: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var rating: Float? = null,
    var phextension: String? = null,
    var openingTime: String? = null,
    var closingTime: String? = null,
    var daysopen: String? = null,
    var address: String? = null,
)
