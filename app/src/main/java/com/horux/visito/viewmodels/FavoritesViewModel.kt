package com.horux.visito.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.repositories.HomeRepository

class FavoritesViewModel : ViewModel() {
    var repository: HomeRepository? = null
    var favorites = MutableLiveData<ArrayList<PlaceModel>?>()
    fun fetchFavorites(viewLifecycleOwner: LifecycleOwner): MutableLiveData<ArrayList<PlaceModel>?> {
        if (repository == null) repository = HomeRepository.instance
        repository!!.fetchFavorites()
            .observe(viewLifecycleOwner!!) { placeModels ->
                if (!placeModels.isEmpty() || favorites.value == null) {
                    favorites.setValue(placeModels)
                } else {
                    favorites.setValue(favorites.value)
                }
            }
        return favorites
    }
}
