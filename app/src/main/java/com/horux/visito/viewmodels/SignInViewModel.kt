package com.horux.visito.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.horux.visito.repositories.SignInRepository

class SignInViewModel : ViewModel() {
    private var repository: SignInRepository? = null
    var email = ""
    var password = ""
    val isLoggedIn: Boolean
        get() {
            if (repository == null) repository = SignInRepository.instance
            return repository!!.isLoggedIn != null
        }

    fun signIn(owner: LifecycleOwner): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        if (repository == null) repository = SignInRepository.instance
        repository!!
            .signIn(email, password)
            .observe(owner, Observer { authResultTask -> response.setValue(authResultTask) })
        return response
    }

    fun signIn(
        owner: LifecycleOwner,
        email: String?,
        password: String?
    ): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        if (repository == null) repository = SignInRepository.instance
        repository!!
            .signIn(email, password)
            .observe(owner, Observer { authResultTask -> response.setValue(authResultTask) })
        return response
    }

    fun forgotPassword(owner: LifecycleOwner): MutableLiveData<Task<Void>> {
        val response: MutableLiveData<Task<Void>> = MutableLiveData()
        if (repository == null) repository = SignInRepository.instance
        repository!!
            .forgotPassword(email)
            .observe(owner, Observer { voidTask -> response.setValue(voidTask) })
        return response
    }

    fun signInWithCredentials(
        owner: LifecycleOwner,
        credential: AuthCredential?
    ): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        if (repository == null) repository = SignInRepository.instance
        repository!!.signInWithCredentials(credential)
            .observe(owner, Observer { authResultTask ->
                if (!authResultTask.isSuccessful()) {
                    repository!!
                        .signInWithCredentials(credential)
                        .observe(
                            owner,
                            Observer { authResultTask -> response.setValue(authResultTask) })
                } else response.setValue(authResultTask)
            })
        return response
    }

    fun signInWithGoogle(
        owner: LifecycleOwner,
        email: String?,
        password: String?
    ): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        if (repository == null) repository = SignInRepository.instance
        signIn(owner, email, password).observe(owner, Observer { authResultTask ->
            if (!authResultTask.isSuccessful()) {
                repository!!
                    .signInGoogle(email, password)
                    .observe(
                        owner,
                        Observer { authResultTask -> response.setValue(authResultTask) })
            } else response.setValue(authResultTask)
        })
        return response
    }
}
