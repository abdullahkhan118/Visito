package com.horux.visito.models.dao

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class UserModel(
    var id: String,
    var email: String,
    var name: String,
    var password: String,
    var phoneNumber: String,
    var token: String,
): Parcelable {

    override fun toString(): String {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}'
    }

    fun toJsonString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromJson(json: String?): UserModel? {
            val gson = Gson()
            var model: UserModel? = null
            try {
                model = gson.fromJson(json, UserModel::class.java)
            } catch (e: Exception) {
            }
            return model
        }
    }
}
