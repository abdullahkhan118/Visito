package com.horux.visito.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.repositories.HomeRepository

class HotelsViewModel : ViewModel() {
    var repository: HomeRepository? = null
    var popularSelected = true
    var hotels = MutableLiveData<ArrayList<PlaceModel>>()
    fun fetchHotels(): MutableLiveData<ArrayList<PlaceModel>> {
        if (repository == null) repository = HomeRepository.instance
        hotels = repository!!.fetchHotels()
        return hotels
    }
}
