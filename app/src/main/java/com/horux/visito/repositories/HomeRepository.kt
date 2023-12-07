package com.horux.visito.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.horux.visito.globals.AppConstants
import com.horux.visito.models.dao.EventModel
import com.horux.visito.models.dao.MessageModel
import com.horux.visito.models.dao.UserModel
import com.horux.visito.models.dao.PlaceModel

class HomeRepository private constructor() {
    var user: MutableLiveData<UserModel> = MutableLiveData<UserModel>()
        get() {
            val id: String = firebaseAuth.getCurrentUser()!!.getUid()
            firebaseFirestore
                .collection(AppConstants.STRING_USERS)
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                    override fun onComplete(task: Task<QuerySnapshot?>) {
                        Log.e("GetUser", "Task successful: " + task.isSuccessful())
                        if (task.isSuccessful()) {
                            Log.e(
                                "GetUser",
                                "Task documents: " + task.getResult()!!.getDocuments().isEmpty()
                            )
                            if (!task.getResult()!!.getDocuments().isEmpty()) {
                                field.setValue(
                                    task.getResult()!!.getDocuments().get(0)
                                        .toObject(UserModel::class.java)
                                )
                            }
                        }
                    }
                })
            return field
        }
    private val firebaseAuth: FirebaseAuth
    private val firebaseFirestore: FirebaseFirestore
    private val firebaseDatabase: FirebaseDatabase

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
    }

    fun updateUser(userModel: UserModel): MutableLiveData<UserModel> {
        val id: String = firebaseAuth.getCurrentUser()!!.getUid()
        val user: MutableLiveData<UserModel> = MutableLiveData<UserModel>()
        try {
            firebaseFirestore
                .collection(AppConstants.STRING_USERS)
                .document(id)
                .set(userModel)
                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                    override fun onComplete(task: Task<Void?>) {
                        if (task.isSuccessful()) {
                            Log.e("UserData", "Added")
                            try {
                                firebaseAuth.getCurrentUser()!!
                                    .updatePassword(userModel.password!!)
                                    .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                        override fun onComplete(task: Task<Void?>) {
                                            if (task.isSuccessful()) {
                                                Log.e("UserData", "Password Changed")
                                                user.setValue(userModel)
                                                this@HomeRepository.user.setValue(userModel)
                                            } else {
                                                Log.e("UserData", "Password Unchanged")
                                                user.setValue(user.getValue())
                                            }
                                        }
                                    })
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else user.setValue(user.getValue())
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return user
    }

    fun updatePlace(placeModel: PlaceModel) {
        val id: String = firebaseAuth.getCurrentUser()!!.getUid()
        var collection: String = AppConstants.STRING_RESTAURANTS
        if (placeModel.image!!.contains("places")) collection =
            AppConstants.STRING_PLACES else if (placeModel.image!!
                .contains("hotels")
        ) collection = AppConstants.STRING_HOTELS
        firebaseFirestore
            .collection(collection)
            .document(placeModel.id!!)
            .set(placeModel)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {}
            })
    }

    fun fetchPlaces(): MutableLiveData<ArrayList<PlaceModel>> {
        val places: MutableLiveData<ArrayList<PlaceModel>> =
            MutableLiveData<ArrayList<PlaceModel>>()
        firebaseFirestore
            .collection(AppConstants.STRING_PLACES)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    if (task.isSuccessful()) {
                        val placeModelArrayList: ArrayList<PlaceModel> = ArrayList<PlaceModel>()
                        val querySnapshot: QuerySnapshot = task.getResult()!!
                        if (!querySnapshot.isEmpty()) {
                            for (documentSnapshot in querySnapshot.getDocuments()) {
                                try {
                                    val placeModel: PlaceModel =
                                        documentSnapshot.toObject(PlaceModel::class.java)!!
                                    if (placeModel != null) placeModel.id = (documentSnapshot.id!!)
                                    Log.e("Place", placeModel.toString())
                                    placeModelArrayList.add(placeModel)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            places.setValue(placeModelArrayList)
                        }
                    }
                }
            })
        return places
    }

    fun fetchRestaurants(): MutableLiveData<ArrayList<PlaceModel>> {
        val restaurants: MutableLiveData<ArrayList<PlaceModel>> =
            MutableLiveData<ArrayList<PlaceModel>>()
        firebaseFirestore
            .collection(AppConstants.STRING_RESTAURANTS)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    if (task.isSuccessful()) {
                        val placeModelArrayList: ArrayList<PlaceModel> = ArrayList<PlaceModel>()
                        val querySnapshot: QuerySnapshot = task.getResult()!!
                        if (!querySnapshot.isEmpty()) {
                            for (documentSnapshot in querySnapshot.getDocuments()) {
                                try {
                                    val placeModel: PlaceModel =
                                        documentSnapshot.toObject(PlaceModel::class.java)!!
                                    if (placeModel != null) placeModel.id = (documentSnapshot.id!!)
                                    Log.e("Restaurant", placeModel.toString())
                                    placeModelArrayList.add(placeModel)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            restaurants.setValue(placeModelArrayList)
                        }
                    }
                }
            })
        return restaurants
    }

    fun fetchHotels(): MutableLiveData<ArrayList<PlaceModel>> {
        val hotels: MutableLiveData<ArrayList<PlaceModel>> =
            MutableLiveData<ArrayList<PlaceModel>>()
        firebaseFirestore
            .collection(AppConstants.STRING_HOTELS)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    if (task.isSuccessful()) {
                        val placeModelArrayList: ArrayList<PlaceModel> = ArrayList<PlaceModel>()
                        val querySnapshot: QuerySnapshot = task.getResult()!!
                        if (!querySnapshot.isEmpty()) {
                            for (documentSnapshot in querySnapshot.getDocuments()) {
                                try {
                                    val placeModel: PlaceModel =
                                        documentSnapshot.toObject(PlaceModel::class.java)!!
                                    if (placeModel != null) placeModel.id = (documentSnapshot.id!!)
                                    Log.e("Hotel", placeModel.toString())
                                    placeModelArrayList.add(placeModel)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            hotels.setValue(placeModelArrayList)
                        }
                    }
                }
            })
        return hotels
    }

    fun addFavorite(placeModel: PlaceModel): MutableLiveData<Boolean> {
        val favoriteAdded: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val userModel: UserModel = user.getValue()!!
        Log.e("UserModel", "" + (userModel != null))
        if (userModel != null) {
            firebaseFirestore
                .collection(AppConstants.STRING_USERS)
                .document(userModel.id!!)
                .collection(AppConstants.STRING_FAVORITES)
                .document(placeModel.id!!)
                .set(placeModel)
                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                    override fun onSuccess(unused: Void?) {
                        favoriteAdded.setValue(true)
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        Log.e("FavException", e.message!!)
                        favoriteAdded.setValue(false)
                    }
                })
        }
        return favoriteAdded
    }

    fun removeFavorite(placeModel: PlaceModel): MutableLiveData<Boolean> {
        val favoriteRemove: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val userModel: UserModel = user.getValue()!!
        if (userModel != null) {
            firebaseFirestore
                .collection(AppConstants.STRING_USERS)
                .document(userModel.id!!)
                .collection(AppConstants.STRING_FAVORITES)
                .document(placeModel.id!!)
                .delete()
                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                    override fun onComplete(task: Task<Void?>) {
                        favoriteRemove.setValue(task.isSuccessful())
                    }
                })
        }
        return favoriteRemove
    }

    fun fetchFavorites(): MutableLiveData<ArrayList<PlaceModel>> {
        val favorites: MutableLiveData<ArrayList<PlaceModel>> =
            MutableLiveData<ArrayList<PlaceModel>>()
        val userModel: UserModel = user.getValue()!!
        if (userModel != null) {
            firebaseFirestore
                .collection(AppConstants.STRING_USERS)
                .document(userModel.id!!)
                .collection(AppConstants.STRING_FAVORITES)
                .get()
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                    override fun onComplete(task: Task<QuerySnapshot?>) {
                        Log.e("fetchFavorite", task.isSuccessful().toString())
                        if (task.isSuccessful()) {
                            val placeModelArrayList: ArrayList<PlaceModel> = ArrayList<PlaceModel>()
                            val querySnapshot: QuerySnapshot = task.getResult()!!
                            if (!querySnapshot.isEmpty()) {
                                for (documentSnapshot in querySnapshot.getDocuments()) {
                                    try {
                                        val placeModel: PlaceModel = documentSnapshot.toObject(
                                            PlaceModel::class.java
                                        )!!
                                        if (placeModel != null) placeModel.id = (documentSnapshot.id!!)
                                        Log.e("Place", placeModel.toString())
                                        placeModelArrayList.add(placeModel)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                favorites.setValue(placeModelArrayList)
                            } else favorites.setValue(ArrayList<PlaceModel>())
                        } else favorites.setValue(ArrayList<PlaceModel>())
                    }
                })
        }
        return favorites
    }

    fun isFavorite(placeModel: PlaceModel): MutableLiveData<Boolean> {
        val isFavorite: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val userModel: UserModel = user.getValue()!!
        if (userModel != null) {
            firebaseFirestore
                .collection(AppConstants.STRING_USERS)
                .document(userModel.id!!)
                .collection(AppConstants.STRING_FAVORITES)
                .whereEqualTo("id", placeModel.id!!)
                .get()
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                    override fun onComplete(task: Task<QuerySnapshot?>) {
                        Log.e("isFavorite", task.isSuccessful().toString())
                        if (task.isSuccessful()) {
                            Log.e("isFavorite", task.getResult().toString())
                            isFavorite.setValue(task.getResult() != null)
                        } else isFavorite.setValue(false)
                    }
                })
        } else isFavorite.setValue(false)
        return isFavorite
    }

    fun fetchEvents(): MutableLiveData<ArrayList<EventModel>> {
        val events: MutableLiveData<ArrayList<EventModel>> =
            MutableLiveData<ArrayList<EventModel>>()
        firebaseFirestore
            .collection(AppConstants.STRING_EVENTS)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    if (task.isSuccessful()) {
                        val eventModelArrayList: ArrayList<EventModel> = ArrayList<EventModel>()
                        val querySnapshot: QuerySnapshot = task.getResult()!!
                        if (!querySnapshot.isEmpty()) {
                            for (documentSnapshot in querySnapshot.getDocuments()) {
                                try {
                                    val eventModel: EventModel =
                                        documentSnapshot.toObject(EventModel::class.java)!!
                                    if (eventModel != null) eventModel.id = (documentSnapshot.id!!)
                                    Log.e("Event", eventModel.toString())
                                    eventModelArrayList.add(eventModel)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            events.setValue(eventModelArrayList)
                        }
                    }
                }
            })
        return events
    }

    fun sendMessage(messageModel: MessageModel?): MutableLiveData<MessageModel> {
        val message: MutableLiveData<MessageModel> = MutableLiveData<MessageModel>()
        firebaseFirestore
            .collection(AppConstants.STRING_QUERIES)
            .add(messageModel!!)
            .addOnCompleteListener(object : OnCompleteListener<DocumentReference?> {
                override fun onComplete(task: Task<DocumentReference?>) {
                    if (task.isSuccessful()) task.getResult()!!.get()
                        .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot?> {
                            override fun onComplete(task: Task<DocumentSnapshot?>) {
                                if (task.isSuccessful()) message.setValue(
                                    task.getResult()!!.toObject(
                                        MessageModel::class.java
                                    )
                                )
                            }
                        })
                }
            })
        return message
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    companion object {
        private var repository: HomeRepository? = null
        val instance: HomeRepository?
            get() {
                if (repository == null) {
                    repository = HomeRepository()
                }
                return repository
            }
    }
}
