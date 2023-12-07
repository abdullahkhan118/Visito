package com.horux.visito.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.UserModel
import com.horux.visito.repositories.HomeRepository

class HomeViewModel : ViewModel() {
    private var repository: HomeRepository? = null
    val user: MutableLiveData<UserModel>
        get() {
            if (repository == null) repository = HomeRepository.instance
            return repository!!.user
        }

    fun updateUser(userModel: UserModel): MutableLiveData<UserModel> {
        if (repository == null) repository = HomeRepository.instance
        return repository!!.updateUser(userModel)
    }

    fun logout() {
        if (repository == null) repository = HomeRepository.instance
        repository!!.logout()
    }
}
