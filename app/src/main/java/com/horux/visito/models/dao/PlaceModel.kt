package com.horux.visito.models.dao

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceModel(
    var id: String,
    var image: String,
    var title: String,
    var description: String,
    var latitude: Double,
    var longitude: Double,
    var rating: Float,
    var phextension: String,
    var openingTime: String,
    var closingTime: String,
    var daysopen: String,
    var address: String,
): Parcelable