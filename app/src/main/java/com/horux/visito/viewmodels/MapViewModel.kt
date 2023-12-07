package com.horux.visito.viewmodels

import android.location.Location
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng

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
        viewLifecycleOwner: LifecycleOwner?
    ): MutableLiveData<AutoCompleteResponse> {
        if (repository == null) repository = ApiRepository.Companion.getInstance()
        repository
            .getAutoComplete(query, radiusInKM, latLng.latitude as Float, latLng.longitude as Float)
            .observe(
                viewLifecycleOwner,
                Observer<Any?> { autoCompleteResponse ->
                    if (autoCompleteResponse != null) autoComplete.setValue(autoCompleteResponse) else autoComplete.setValue(
                        autoCompleteResponse
                    )
                })
        return autoComplete
    }

    fun fetchCategories(): MutableLiveData<ArrayList<String>> {
        if (repository == null) repository = ApiRepository.Companion.getInstance()
        categories = repository.fetchCategories()
        return categories
    }

    fun weatherForecast(viewLifecycleOwner: LifecycleOwner?): MutableLiveData<CurrentWeatherResponse> {
        if (repository == null) repository = ApiRepository.Companion.getInstance()
        repository.getCurrentWeather().observe(
            viewLifecycleOwner,
            Observer<Any?> { currentWeatherResponse -> weather.setValue(currentWeatherResponse) })
        return weather
    }

    fun fetchRoutes(destinationLatLng: LatLng?): MutableLiveData<RouteResponse> {
        destination = destinationLatLng
        if (currentLocation != null) {
            val currentLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            if (repository == null) repository = ApiRepository.Companion.getInstance()
            route = repository.getRoute(currentLatLng, destinationLatLng)
        }
        return route
    }
}
