package com.horux.visito.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.horux.visito.globals.AppConstants
import com.horux.visito.globals.UserGlobals
import com.horux.visito.models.dao.UserModel

class SignInRepository private constructor() {
    private val firebaseAuth: FirebaseAuth
    private val firebaseFirestore: FirebaseFirestore

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    val isLoggedIn: FirebaseUser
        get() = firebaseAuth.getCurrentUser()!!

    fun signIn(email: String?, password: String?): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        firebaseAuth
            .signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        UserGlobals.user = task.getResult()!!.getUser()
                    }
                    response.setValue(task)
                }
            })
        return response
    }

    fun forgotPassword(email: String?): MutableLiveData<Task<Void>> {
        val response: MutableLiveData<Task<Void>> = MutableLiveData()
        firebaseAuth
            .sendPasswordResetEmail(email!!)
            .addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    response.setValue(task)
                }
            })
        return response
    }

    fun signInWithCredentials(credential: AuthCredential?): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        firebaseAuth.signInWithCredential(credential!!)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    Log.e("SignInWithGoogle", task.isSuccessful().toString())
                    if (task.isSuccessful()) {
                        UserGlobals.user = firebaseAuth.getCurrentUser()
                        val id: String = task.getResult().getUser()!!.getUid()
                        val userModel = UserModel(
                            id,
                            UserGlobals.user!!.getEmail(),
                            if (UserGlobals.user!!.getDisplayName() != null) UserGlobals.user!!.getDisplayName() else "",
                            "",
                            if (UserGlobals.user!!.getPhoneNumber() != null) UserGlobals.user!!.getPhoneNumber() else "",
                            ""
                        )
                        firebaseFirestore
                            .collection(AppConstants.STRING_USERS)
                            .document(id)
                            .set(userModel)
                            .addOnCompleteListener(object : OnCompleteListener<Void> {
                                override fun onComplete(taskVoid: Task<Void>) {
                                    Log.e(
                                        "GoogleUser",
                                        task.isSuccessful().toString() +" " + task.isCanceled()
                                    )
                                    if (!task.isSuccessful() || task.isCanceled()) {
                                        response.setValue(null)
                                        task.getResult().getUser()!!.delete()
                                    }
                                }
                            })
                    }
                    response.setValue(task)
                }
            })
        return response
    }

    fun signInGoogle(email: String?, password: String?): MutableLiveData<Task<AuthResult>> {
        val response: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        firebaseAuth
            .createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        UserGlobals.user = firebaseAuth.getCurrentUser()
                        val id: String = task.getResult().getUser()!!.getUid()
                        val userModel = UserModel(
                            id,
                            UserGlobals.user!!.getEmail(),
                            if (UserGlobals.user!!.getDisplayName() != null) UserGlobals.user!!.getDisplayName() else "",
                            "",
                            if (UserGlobals.user!!.getPhoneNumber() != null) UserGlobals.user!!.getPhoneNumber() else "",
                            ""
                        )
                        firebaseFirestore
                            .collection(AppConstants.STRING_USERS)
                            .document(id)
                            .set(userModel)
                            .addOnCompleteListener(object : OnCompleteListener<Void> {
                                override fun onComplete(taskVoid: Task<Void>) {
                                    if (!task.isSuccessful() || task.isCanceled()) {
                                        response.setValue(null)
                                        task.getResult().getUser()!!.delete()
                                    }
                                }
                            })
                    }
                    response.setValue(task)
                }
            })
        return response
    } //    public MutableLiveData<Task<AuthResult>> signInTwitter(String email, String password) {

    //        MutableLiveData<Task<AuthResult>> response = new MutableLiveData();
    //        firebaseAuth
    //                .createUserWithEmailAndPassword(email, password)
    //                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    //                    @Override
    //                    public void onComplete(@NonNull Task<AuthResult> task) {
    //                        response.setValue(task);
    //                    }
    //                });
    //        return response;
    //    }
    companion object {
        private var repository: SignInRepository? = null
        val instance: SignInRepository?
            get() {
                if (repository == null) {
                    repository = SignInRepository()
                }
                return repository
            }
    }
}
