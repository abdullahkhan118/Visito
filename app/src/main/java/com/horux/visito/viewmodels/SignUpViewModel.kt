package com.horux.visito.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.horux.visito.repositories.SignUpRepository

class SignUpViewModel : ViewModel() {
    private var repository: SignUpRepository? = null
    var email = ""
    var password = ""
    var fullName = ""
    var phoneNumber = ""
    fun signUp(owner: LifecycleOwner): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData<Task<AuthResult>>()
        if (repository == null) repository = SignUpRepository.instance
        repository!!
            .signUp(email, password, fullName, phoneNumber)
            .observe(owner, Observer { authResultTask -> response.setValue(authResultTask) })
        return response
    }

    fun checkValidations(activity: AppCompatActivity?): Boolean {
        return true
    }
}
