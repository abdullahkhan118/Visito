package com.horux.visito.viewmodels

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.horux.visito.models.dao.EventModel
import com.horux.visito.repositories.ApiRepository
import com.horux.visito.repositories.HomeRepository

class EventDescriptionViewModel : ViewModel() {
    var location: MutableLiveData<Location> = MutableLiveData<Location>()
    private val homeRepository: HomeRepository? = null
    private var apiRepository: ApiRepository? = null
    var event: MutableLiveData<EventModel> = MutableLiveData<EventModel>()
        set(eventModel) {
            event.setValue(eventModel.value)
        }
    private val distance: MutableLiveData<Float> = MutableLiveData<Float>()
    fun setDistance(
        currentLatLng: LatLng?,
        placeLatLng: LatLng?,
        owner: LifecycleOwner?
    ): MutableLiveData<Float> {
        if (apiRepository == null) apiRepository = ApiRepository.instance
        apiRepository
            .getRoute(currentLatLng, placeLatLng)
            .observe(owner, Observer<Any?> { routeResponse ->
                if (routeResponse != null) {
                    val lengthInMeters: Long =
                        routeResponse.getRoutes().get(0).getSummary().getLengthInMeters()
                    distance.setValue(lengthInMeters / 1000f)
                }
            })
        return distance
    }

    fun getDistance(): MutableLiveData<Float> {
        return distance
    }
}
