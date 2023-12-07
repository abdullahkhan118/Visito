package com.horux.visito.networking

import com.horux.visito.models.tomtom.autocomplete.AutoCompleteResponse
import com.horux.visito.models.tomtom.categories.CategoryResponse
import com.horux.visito.models.weather.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.horux.visito.models.tomtom.route.RouteResponse

interface WebServices {
    @GET(
        "search/2/search/{autocomplete}.json?limit=100&" +
                "minFuzzyLevel=2&" +
                "maxFuzzyLevel=3&" +
                "view=Unified&" +
                "relatedPois=off&" +
                "key=1oAirRLOnqYjvPSblLJzCl47fccUktb3"
    )
    fun fetchAutoComplete(
        @Path("autocomplete") autoComplete: String?,
        @Query("radius") radius: Int,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float
    ): Call<AutoCompleteResponse?>?

    @GET("search/2/poiCategories.json?key=1oAirRLOnqYjvPSblLJzCl47fccUktb3")
    fun fetchCategories(): Call<CategoryResponse?>?

    @GET(
        "routing/1/calculateRoute/{locations}/json?" +
                "maxAlternatives=0&" +
                "instructionsType=text&" +
                "language=en-GB&" +
                "computeBestOrder=true&" +
                "routeRepresentation=polyline&" +
                "computeTravelTimeFor=all&" +
                "sectionType=travelMode&" +
                "departAt=now&routeType=fastest&" +
                "traffic=true&" +
                "avoid=unpavedRoads&" +
                "travelMode=car&" +
                "key=1oAirRLOnqYjvPSblLJzCl47fccUktb3"
    )
    fun fetchRoute(@Path("locations") locations: String?): Call<RouteResponse?>?

    @GET("current.json?key=ab1e19de29fc494db3f185340222001")
    fun fetchWeather(@Query("q") query: String?): Call<CurrentWeatherResponse?>?
}
