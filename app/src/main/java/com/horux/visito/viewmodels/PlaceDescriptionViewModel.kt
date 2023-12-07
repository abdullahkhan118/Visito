package com.horux.visito.viewmodels

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.models.tomtom.route.RouteResponse
import com.horux.visito.repositories.ApiRepository
import com.horux.visito.repositories.HomeRepository

class PlaceDescriptionViewModel : ViewModel() {
    var location: MutableLiveData<Location> = MutableLiveData<Location>()
    var isFavorite: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    var rating: MutableLiveData<Float> = MutableLiveData<Float>(0f)
    private var homeRepository: HomeRepository? = null
    private var apiRepository: ApiRepository? = null
    private val place: MutableLiveData<PlaceModel> = MutableLiveData<PlaceModel>()
    private val distance: MutableLiveData<Float> = MutableLiveData<Float>()
    fun getPlace(): MutableLiveData<PlaceModel> {
        return place
    }

    fun setPlace(placeModel: PlaceModel?) {
        place.setValue(placeModel)
    }

    fun updatePlaceRating() {
        if (homeRepository == null) homeRepository = HomeRepository.instance
        if (place.getValue() != null) {
            homeRepository!!.updatePlace(place.getValue()!!)
        }
    }

    fun isFavorite(owner: LifecycleOwner): MutableLiveData<Boolean> {
        if (homeRepository == null) homeRepository = HomeRepository.instance
        if (place.getValue() != null) {
            homeRepository!!.isFavorite(place.getValue()!!).observe(owner
            ) { favorite -> isFavorite.setValue(favorite) }
        }
        return isFavorite
    }

    fun setDistance(
        currentLatLng: LatLng,
        placeLatLng: LatLng,
        owner: LifecycleOwner
    ): MutableLiveData<Float> {
        val current: String = currentLatLng.latitude.toString() + "," + currentLatLng.longitude
        val place: String = placeLatLng.latitude.toString() + "," + placeLatLng.longitude
        if (apiRepository == null) apiRepository = ApiRepository.instance
        apiRepository!!
            .getRoute(currentLatLng, placeLatLng)
            .observe(owner, Observer<RouteResponse> { routeResponse ->
                if (routeResponse != null) {
                    val lengthInMeters: Long =
                        routeResponse.routes!!.get(0).summary!!.lengthInMeters!!
                    distance.setValue(lengthInMeters / 1000f)
                } else {
                    val theta: Double = currentLatLng.longitude - placeLatLng.longitude
                    var dist = (Math.sin(currentLatLng.latitude * Math.PI / 180.0)
                            * Math.sin(placeLatLng.latitude * Math.PI / 180.0)
                            + (Math.cos(currentLatLng.latitude * Math.PI / 180.0)
                            * Math.cos(placeLatLng.latitude * Math.PI / 180.0)
                            * Math.cos(theta * Math.PI / 180.0)))
                    dist = Math.acos(dist)
                    dist = dist * 180.0 / Math.PI
                    dist = dist * 60 * 1.1515
                    distance.setValue(dist.toFloat() / 1000f)
                }
            })
        return distance
    }

    fun getDistance(): MutableLiveData<Float> {
        return distance
    }

    fun addFavorite() {
        if (homeRepository == null) homeRepository = HomeRepository.instance
        homeRepository!!.addFavorite(place.getValue()!!)
    }

    fun removeFavorite() {
        if (homeRepository == null) homeRepository = HomeRepository.instance
        homeRepository!!.removeFavorite(place.getValue()!!)
    }
}
