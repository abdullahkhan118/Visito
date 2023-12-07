package com.horux.visito.models.dao

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModel(
    var id: String,
    var image: String,
    var title: String,
    var description: String,
    var latitude: Double,
    var longitude: Double,
    var startTime: String,
    var endTime: String,
    var address: String,
) : Parcelable {
    companion object {
        fun fromJson(json: String?): EventModel? {
            val gson = Gson()
            var model: EventModel? = null
            try {
                model = gson.fromJson(json, EventModel::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return model
        }
    }
}
