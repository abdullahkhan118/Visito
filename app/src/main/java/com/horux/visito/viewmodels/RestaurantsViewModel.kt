package com.horux.visito.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.repositories.HomeRepository

class RestaurantsViewModel : ViewModel() {
    var repository: HomeRepository? = null
    var popularSelected = true
    var restaurants = MutableLiveData<ArrayList<PlaceModel>>()
    fun fetchRestaurants(): MutableLiveData<ArrayList<PlaceModel>> {
        if (repository == null) repository = HomeRepository.instance
        restaurants = repository!!.fetchRestaurants()
        return restaurants
    }
}
