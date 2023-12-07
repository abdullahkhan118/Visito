package com.horux.visito.viewmodels

import android.app.Activity
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.Task

class SignUpViewModel : ViewModel() {
    private var repository: SignUpRepository? = null
    var email = ""
    var password = ""
    var fullName = ""
    var phoneNumber = ""
    fun signUp(owner: LifecycleOwner?): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData<Task<AuthResult>>()
        if (repository == null) repository = SignUpRepository.Companion.getInstance()
        repository
            .signUp(email, password, fullName, phoneNumber)
            .observe(owner, Observer<Any?> { authResultTask -> response.setValue(authResultTask) })
        return response
    }

    fun checkValidations(activity: Activity?): Boolean {
        return true
    }
}
