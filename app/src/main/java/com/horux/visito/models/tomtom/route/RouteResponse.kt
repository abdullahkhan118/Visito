package com.horux.visito.models.tomtom.route

import com.google.gson.annotations.SerializedName

class RouteResponse {
    @SerializedName("routes")
    var routes: List<Route>? = null
    override fun toString(): String {
        return "RouteResponse{" +
                "mRoutes=" + routes.toString() +
                '}'
    }
}
