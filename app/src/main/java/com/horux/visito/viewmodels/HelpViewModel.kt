package com.horux.visito.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.MessageModel
import com.horux.visito.repositories.HomeRepository

class HelpViewModel : ViewModel() {
    var message: MutableLiveData<MessageModel?> = MutableLiveData<Any?>(MessageModel(""))
    private var repository: HomeRepository? = null
    fun sendMessage(): MutableLiveData<MessageModel> {
        if (repository == null) repository = HomeRepository.Companion.getInstance()
        return repository!!.sendMessage(message.value)
    }
}
