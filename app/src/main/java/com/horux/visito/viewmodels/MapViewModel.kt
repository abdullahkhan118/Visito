package com.horux.visito.viewmodels

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.horux.visito.models.tomtom.autocomplete.AutoCompleteResponse
import com.horux.visito.models.tomtom.route.RouteResponse
import com.horux.visito.models.weather.CurrentWeatherResponse
import com.horux.visito.repositories.ApiRepository

class MapViewModel : ViewModel() {
    var repository: ApiRepository? = null
    var weather: MutableLiveData<CurrentWeatherResponse> = MutableLiveData<CurrentWeatherResponse>()
    var autoComplete: MutableLiveData<AutoCompleteResponse> =
        MutableLiveData<AutoCompleteResponse>()
    var route: MutableLiveData<RouteResponse> = MutableLiveData<RouteResponse>()
    var categories: MutableLiveData<ArrayList<String>> = MutableLiveData<ArrayList<String>>()
    var currentLocation: Location? = null
    var destination: LatLng? = null
    fun autoComplete(
        query: String?,
        radiusInKM: Int,
        latLng: LatLng,
        viewLifecycleOwner: LifecycleOwner
    ): MutableLiveData<AutoCompleteResponse> {
        if (repository == null) repository = ApiRepository.instance
        repository!!
            .getAutoComplete(query, radiusInKM, latLng.latitude as Float, latLng.longitude as Float)
            .observe(
                viewLifecycleOwner,
                Observer<AutoCompleteResponse> { autoCompleteResponse ->
                    if (autoCompleteResponse != null) autoComplete.setValue(autoCompleteResponse) else autoComplete.setValue(
                        autoCompleteResponse
                    )
                })
        return autoComplete
    }

    fun fetchCategories(): MutableLiveData<ArrayList<String>> {
        if (repository == null) repository = ApiRepository.instance
        categories = repository!!.fetchCategories()
        return categories
    }

    fun weatherForecast(viewLifecycleOwner: LifecycleOwner): MutableLiveData<CurrentWeatherResponse> {
        if (repository == null) repository = ApiRepository.instance
        repository!!.currentWeather.observe(
            viewLifecycleOwner,
            Observer<CurrentWeatherResponse> { currentWeatherResponse -> weather.setValue(currentWeatherResponse) })
        return weather
    }

    fun fetchRoutes(destinationLatLng: LatLng?): MutableLiveData<RouteResponse> {
        destination = destinationLatLng
        if (currentLocation != null) {
            val currentLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            if (repository == null) repository = ApiRepository.instance
            route = repository!!.getRoute(currentLatLng, destinationLatLng!!)
        }
        return route
    }
}
