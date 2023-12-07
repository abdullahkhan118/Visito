package com.horux.visito.models.dao

import com.google.gson.Gson
import org.json.JSONObject

class UserModel {
    var id: String? = null
    var email: String? = null
    var name: String? = null
    var password: String? = null
    var phoneNumber: String? = null
    var token: String? = null

    constructor()
    constructor(
        id: String?,
        email: String?,
        name: String?,
        password: String?,
        phoneNumber: String?,
        token: String?
    ) {
        this.id = id
        this.email = email
        this.name = name
        this.password = password
        this.phoneNumber = phoneNumber
        this.token = token
    }

    override fun toString(): String {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}'
    }

    @Throws(JSONException::class)
    fun toHashMap(): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        val gson = Gson()
        val jsonObject = JSONObject(toJsonString())
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            hashMap[key] = jsonObject[key]
        }
        return hashMap
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
