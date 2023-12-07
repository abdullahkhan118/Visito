package com.horux.visito.models.dao

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

class EventModel : Parcelable {
    var id: String? = null
    var image: String? = null
    var title: String? = null
    var description: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var startTime: String? = null
    var endTime: String? = null
    var address: String? = null

    constructor()
    constructor(
        id: String?,
        image: String?,
        title: String?,
        description: String?,
        latitude: Double?,
        longitude: Double?,
        startTime: String?,
        endTime: String?,
        address: String?
    ) {
        this.id = id
        this.image = image
        this.title = title
        this.description = description
        this.latitude = latitude
        this.longitude = longitude
        this.startTime = startTime
        this.endTime = endTime
        this.address = address
    }

    constructor(
        image: String?,
        title: String?,
        description: String?,
        latitude: Double?,
        longitude: Double?,
        startTime: String?,
        endTime: String?,
        address: String?
    ) {
        this.image = image
        this.title = title
        this.description = description
        this.latitude = latitude
        this.longitude = longitude
        this.startTime = startTime
        this.endTime = endTime
        this.address = address
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readString()
        image = `in`.readString()
        title = `in`.readString()
        description = `in`.readString()
        latitude = if (`in`.readByte().toInt() == 0) {
            null
        } else {
            `in`.readDouble()
        }
        longitude = if (`in`.readByte().toInt() == 0) {
            null
        } else {
            `in`.readDouble()
        }
        startTime = `in`.readString()
        endTime = `in`.readString()
        address = `in`.readString()
    }

    fun toJsonString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(image)
        dest.writeString(title)
        dest.writeString(description)
        if (latitude == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeDouble(latitude!!)
        }
        if (longitude == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeDouble(longitude!!)
        }
        dest.writeString(startTime)
        dest.writeString(endTime)
        dest.writeString(address)
    }

    companion object {
        val CREATOR: Parcelable.Creator<EventModel?> = object : Parcelable.Creator<EventModel?> {
            override fun createFromParcel(`in`: Parcel): EventModel {
                return EventModel(`in`)
            }

            override fun newArray(size: Int): Array<EventModel> {
                return Array(size, { EventModel() })
            }
        }

        fun fromJson(json: String?): EventModel? {
            val gson = Gson()
            var model: EventModel? = null
            try {
                model = gson.fromJson(json, EventModel::class.java)
            } catch (e: Exception) {
            }
            return model
        }
    }

    object CREATOR : Parcelable.Creator<EventModel> {
        override fun createFromParcel(parcel: Parcel): EventModel {
            return EventModel(parcel)
        }

        override fun newArray(size: Int): Array<EventModel?> {
            return arrayOfNulls(size)
        }
    }
}
