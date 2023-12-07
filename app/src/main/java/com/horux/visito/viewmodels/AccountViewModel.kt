package com.horux.visito.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.UserModel
import com.horux.visito.services.FCMService

class AccountViewModel : ViewModel() {
    private var userModel: UserModel? = null
    fun getUserModel(): UserModel? {
        if (userModel != null) Log.e("getUserModel", userModel.toString())
        return userModel
    }

    fun setUserModel(userModel: UserModel?) {
        this.userModel = userModel
    }

    fun setData(email: String?, name: String?, password: String?, phoneNumber: String?) {
        Log.e("UpdatedName", name!!)
        Log.e("UpdatedPassword", password!!)
        Log.e("UpdatedPhoneNumber", phoneNumber!!)
        userModel = UserModel(
            userModel!!.id,
            email,
            name,
            password,
            phoneNumber,
            FCMService.Companion.token
        )
        Log.e("UserData", "Updated")
        Log.e("User", userModel.toString())
    }
}
