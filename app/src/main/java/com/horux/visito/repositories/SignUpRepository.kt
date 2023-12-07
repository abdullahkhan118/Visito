package com.horux.visito.repositories

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.horux.visito.globals.AppConstants
import com.horux.visito.models.dao.UserModel

class SignUpRepository private constructor() {
    private val firebaseAuth: FirebaseAuth
    private val firebaseFirestore: FirebaseFirestore

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    fun signUp(
        email: String?,
        password: String?,
        fullName: String?,
        phoneNumber: String?
    ): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        firebaseAuth
            .createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(authResultTask: Task<AuthResult>) {
                    if (authResultTask.isSuccessful()) {
                        // Add User to firestore
                        val id: String = authResultTask.getResult().getUser()!!.getUid()
                        val userModel = UserModel(
                            id,
                            email,
                            fullName,
                            password,
                            phoneNumber,
                            ""
                        )
                        firebaseFirestore
                            .collection(AppConstants.STRING_USERS)
                            .document(id)
                            .set(userModel)
                            .addOnCompleteListener(object : OnCompleteListener<Void> {
                                override fun onComplete(task: Task<Void>) {
                                    if (task.isSuccessful()) {
                                        response.setValue(authResultTask)
                                    } else {
                                        response.setValue(null)
                                        authResultTask.getResult().getUser()!!.delete()
                                    }
                                }
                            })
                    }
                }
            })
        return response
    }

    companion object {
        private var repository: SignUpRepository? = null
        val instance: SignUpRepository?
            get() {
                if (repository == null) {
                    repository = SignUpRepository()
                }
                return repository
            }
    }
}
