package com.horux.visito.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.horux.visito.models.tomtom.autocomplete.AutoCompleteResponse
import com.horux.visito.models.tomtom.categories.CategoryResponse
import com.horux.visito.models.tomtom.route.RouteResponse
import com.horux.visito.models.weather.CurrentWeatherResponse
import com.horux.visito.networking.WebConstants
import com.horux.visito.networking.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository private constructor() {
    val currentWeather: MutableLiveData<CurrentWeatherResponse>
        get() {
            val currentWeatherResponse: MutableLiveData<CurrentWeatherResponse> =
                MutableLiveData<CurrentWeatherResponse>()
            WebConstants.retrofitWeatherInstance
                ?.create(WebServices::class.java)
                ?.fetchWeather("Karachi")
                ?.enqueue(object : Callback<CurrentWeatherResponse?> {
                    override fun onResponse(
                        call: Call<CurrentWeatherResponse?>?,
                        response: Response<CurrentWeatherResponse?>
                    ) {
                        Log.e("onResponse", "Weather: " + response.body().toString())
                        if (response.isSuccessful()) currentWeatherResponse.setValue(response.body()) else currentWeatherResponse.setValue(
                            null
                        )
                    }

                    override fun onFailure(call: Call<CurrentWeatherResponse?>?, t: Throwable) {
                        Log.e("onFailure", "Weather: " + t.message)
                        currentWeatherResponse.setValue(null)
                    }
                })
            return currentWeatherResponse
        }

    fun getAutoComplete(
        autoComplete: String?,
        radiusInKM: Int,
        lat: Float,
        lon: Float
    ): MutableLiveData<AutoCompleteResponse> {
        val autoCompleteResponse: MutableLiveData<AutoCompleteResponse> =
            MutableLiveData<AutoCompleteResponse>()
        WebConstants.retrofitTomTomInstance
            ?.create(WebServices::class.java)
            ?.fetchAutoComplete(autoComplete, radiusInKM * 1000, lat, lon)
            ?.enqueue(object : Callback<AutoCompleteResponse?> {
                override fun onResponse(
                    call: Call<AutoCompleteResponse?>?,
                    response: Response<AutoCompleteResponse?>
                ) {
                    Log.e("onResponse", "AutoComplete: " + response.toString())
                    val json: String = Gson().toJson(response.body())
                    Log.e("onResponse", "AutoComplete: $json")
                    if (response.isSuccessful()) autoCompleteResponse.setValue(response.body()) else autoCompleteResponse.setValue(
                        null
                    )
                }

                override fun onFailure(call: Call<AutoCompleteResponse?>?, t: Throwable?) {
                    autoCompleteResponse.setValue(null)
                }
            })
        return autoCompleteResponse
    }

    fun fetchCategories(): MutableLiveData<ArrayList<String>> {
        val categories: MutableLiveData<ArrayList<String>> = MutableLiveData<ArrayList<String>>()
        WebConstants.retrofitTomTomInstance
            ?.create(WebServices::class.java)
            ?.fetchCategories()
            ?.enqueue(object : Callback<CategoryResponse?> {
                override fun onResponse(
                    call: Call<CategoryResponse?>?,
                    response: Response<CategoryResponse?>
                ) {
                    if (response.isSuccessful() && response.body() != null) {
                        val categoryList = ArrayList<String>()
                        for (category in (response.body() as CategoryResponse).poiCategories!!) {
                            categoryList.add(category.name!!)
                        }
                        categories.setValue(categoryList)
                    }
                }

                override fun onFailure(call: Call<CategoryResponse?>?, t: Throwable?) {
                    categories.setValue(null)
                }
            })
        return categories
    }

    fun getRoute(sourceLatLng: LatLng, destinationLatLng: LatLng): MutableLiveData<RouteResponse> {
        val locations: String =
            (("${sourceLatLng.latitude} , ${sourceLatLng.longitude}").toString() + ":" + destinationLatLng.latitude).toString() + "," + destinationLatLng.longitude
        val routeResponse: MutableLiveData<RouteResponse> = MutableLiveData<RouteResponse>()
        WebConstants.retrofitTomTomInstance
            ?.create(WebServices::class.java)
            ?.fetchRoute(locations)
            ?.enqueue(object : Callback<RouteResponse?> {
                override fun onResponse(call: Call<RouteResponse?>?, response: Response<RouteResponse?>) {
                    Log.e("onResponse", "Route: " + response.toString())
                    val json: String = Gson().toJson(response.body())
                    Log.e("onResponse", "Route: $json")
                    if (response.isSuccessful()) routeResponse.setValue(response.body()) else routeResponse.setValue(
                        null
                    )
                }

                override fun onFailure(call: Call<RouteResponse?>?, t: Throwable?) {
                    routeResponse.setValue(null)
                }
            })
        return routeResponse
    }

    companion object {
        private var repository: ApiRepository? = null
        val instance: ApiRepository?
            get() {
                if (repository == null) {
                    repository = ApiRepository()
                }
                return repository
            }
    }
}
