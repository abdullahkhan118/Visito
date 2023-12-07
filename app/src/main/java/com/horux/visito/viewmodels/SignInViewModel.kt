package com.horux.visito.viewmodels

import androidx.lifecycle.Observer
import com.google.android.gms.tasks.Task

class SignInViewModel : ViewModel() {
    private var repository: SignInRepository? = null
    var email = ""
    var password = ""
    val isLoggedIn: Boolean
        get() {
            if (repository == null) repository = SignInRepository.Companion.getInstance()
            return repository.isLoggedIn() != null
        }

    fun signIn(owner: LifecycleOwner?): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData<Any?>()
        if (repository == null) repository = SignInRepository.Companion.getInstance()
        repository
            .signIn(email, password)
            .observe(owner, Observer<Any?> { authResultTask -> response.setValue(authResultTask) })
        return response
    }

    fun signIn(
        owner: LifecycleOwner?,
        email: String?,
        password: String?
    ): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData<Any?>()
        if (repository == null) repository = SignInRepository.Companion.getInstance()
        repository
            .signIn(email, password)
            .observe(owner, Observer<Any?> { authResultTask -> response.setValue(authResultTask) })
        return response
    }

    fun forgotPassword(owner: LifecycleOwner?): MutableLiveData<Task<Void>> {
        val response: MutableLiveData<Task<Void>> = MutableLiveData<Any?>()
        if (repository == null) repository = SignInRepository.Companion.getInstance()
        repository
            .forgotPassword(email)
            .observe(owner, Observer<Any?> { voidTask -> response.setValue(voidTask) })
        return response
    }

    fun signInWithCredentials(
        owner: LifecycleOwner?,
        credential: AuthCredential?
    ): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData<Any?>()
        if (repository == null) repository = SignInRepository.Companion.getInstance()
        repository.signInWithCredentials(credential)
            .observe(owner, Observer<Any?> { authResultTask ->
                if (!authResultTask.isSuccessful()) {
                    repository
                        .signInWithCredentials(credential)
                        .observe(
                            owner,
                            Observer<Any?> { authResultTask -> response.setValue(authResultTask) })
                } else response.setValue(authResultTask)
            })
        return response
    }

    fun signInWithGoogle(
        owner: LifecycleOwner?,
        email: String?,
        password: String?
    ): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData<Any?>()
        if (repository == null) repository = SignInRepository.Companion.getInstance()
        signIn(owner, email, password).observe(owner, Observer<Any?> { authResultTask ->
            if (!authResultTask.isSuccessful()) {
                repository
                    .signInGoogle(email, password)
                    .observe(
                        owner,
                        Observer<Any?> { authResultTask -> response.setValue(authResultTask) })
            } else response.setValue(authResultTask)
        })
        return response
    }
}
