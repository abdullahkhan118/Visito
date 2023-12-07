package com.horux.visito.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.repositories.HomeRepository

class PlacesViewModel : ViewModel() {
    var repository: HomeRepository? = null
    var popularSelected = true
    var places = MutableLiveData<ArrayList<PlaceModel>>()
    fun fetchPlaces(): MutableLiveData<ArrayList<PlaceModel>> {
        if (repository == null) repository = HomeRepository.instance
        places = repository!!.fetchPlaces()
        return places
    }
}
