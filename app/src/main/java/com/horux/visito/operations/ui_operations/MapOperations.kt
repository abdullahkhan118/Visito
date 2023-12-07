package com.horux.visito.operations.ui_operations

import androidx.annotation.ColorRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions

object MapOperations {
    //    fun mapInit(map: SupportMapFragment, addListener: Boolean) {
    //        map.getMapAsync(OnMapReadyCallback { gMap ->
    //            if(addListener)
    //                gMap.setOnMapClickListener {
    //                onMapClicked(
    //                        gMap,
    //                        VisitoApplication.myLocation.value!!.toLatLng()
    //                    )
    //            }
    //            VisitoApplication.myLocation.value?.let {
    //                addMarkerToLocation(
    //                        gMap,
    //                        it.toLatLng(),
    //                        "Current Location",
    //                        "Address Unknown lat: ${it.latitude} lng: ${it.longitude}"
    //                )
    //            }
    //            Place.selectedPlace?.let {
    //                addMarkerToLocation(
    //                        gMap,
    //                        it.location.toLatLng(),
    //                        it.name,
    //                        it.description
    //                )
    //            }
    //
    //        })
    //    }
    fun mapInit(map: SupportMapFragment, currentLatLng: LatLng, addListener: Boolean?) {
        map.getMapAsync { googleMap ->
            addMarkerToLocation(
                googleMap,
                currentLatLng,
                "Current Location",
                "Address Unknown lat: \${it.latitude} lng: \${it.longitude}"
            )
        }
    }

    //    fun addMarkerToLocation(
    //            gMap: GoogleMap,
    //            latlng: LatLng,
    //            title: String,
    //            snippets: String
    //    ) {
    //        gMap.addMarker(
    //                MarkerOptions().position(latlng)
    //                        .title(title)
    //                        .snippet(snippets)
    //        )
    //        gMap.addPolygon(
    //                PolygonOptions()
    //                        .clickable(true)
    //                        .add(LatLng(24.9141, 67.1075))
    //                        .add(LatLng(24.9238, 67.0986))
    //                        .add(LatLng(24.8638, 67.0695))
    //                        .fillColor(R.color.green_dark)
    //        )
    //        gMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
    //        gMap.setMinZoomPreference(15f)
    //        gMap.setMaxZoomPreference(50f)
    //    }
    fun addMarkerToLocation(
        gMap: GoogleMap,
        latLng: LatLng,
        title: String,
        snippet: String
    ): MarkerOptions {
        val markerOptions: MarkerOptions =
            MarkerOptions().position(latLng).title(title).snippet(snippet)
        gMap.addMarker(markerOptions)
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        gMap.setMinZoomPreference(15f)
        gMap.setMaxZoomPreference(50f)
        return markerOptions
    }

    fun addPolygon(
        gMap: GoogleMap?,
        latLngs: Array<LatLng?>,
        @ColorRes color: Int
    ): PolygonOptions {
        val polygonOptions = PolygonOptions()
        for (latLng in latLngs) {
            polygonOptions.add(latLng)
        }
        polygonOptions.clickable(false)
        polygonOptions.fillColor(color)
        return polygonOptions
    }

    //    fun onMapClicked(gMap: GoogleMap, currentLatLng: LatLng, radius: Double = 0.0) {
    //        gMap.setOnMapClickListener {
    //            gMap.addMarker(
    //                    MarkerOptions().position(it)
    //                            .title("Marker in Clicked Location")
    //                            .snippet("Address: Unknown\nlat: ${it.latitude} lng: ${it.longitude}\nDistance: ${getDistance(VisitoApplication.myLocation.value!!.toLatLng(),it)}")
    //            )
    //            println("Address: Unknown\nlat: ${it.latitude} lng: ${it.longitude}\nDistance: ${getDistance(VisitoApplication.myLocation.value!!.toLatLng(),it)}")
    //            gMap.addPolygon(
    //                    PolygonOptions()
    //                            .visible(true)
    //                            .clickable(true)
    //                            .fillColor(R.color.green_dark)
    //                            .add(it)
    //                            .add(currentLatLng)
    //                            .add(getCenter(it, currentLatLng))
    //                            .strokeColor(R.color.black)
    //                            .strokeWidth(2f)
    //            )
    //            // if radius is provided then center is current location
    //            // else center is midpoint and radius is half the distance between two latlngs
    //            gMap.addCircle(
    //                    CircleOptions()
    //                            .center(
    //            if (radius > 0) {
    //                LatLng(
    //                        VisitoApplication.myLocation.value!!.latitude,
    //                        VisitoApplication.myLocation.value!!.longitude
    //                                )
    //            } else
    //                getCenter(
    //                        it, LatLng(
    //                                VisitoApplication.myLocation.value!!.latitude,
    //                    VisitoApplication.myLocation.value!!.longitude
    //                                    )
    //                                )
    //                        )
    //                        .clickable(true)
    //                    .strokeColor(R.color.black)
    //                    .strokeWidth(2f)
    //                    .radius(
    //            if (radius > 0) {
    //                radius
    //            } else {
    //                getRadius(
    //                        it, LatLng(
    //                                VisitoApplication.myLocation.value!!.latitude,
    //                        VisitoApplication.myLocation.value!!.longitude
    //                                    )
    //                                )
    //            }
    //                        )
    //                )
    //            // onPolygon Click move camera to it keeping its first and last coordinates in screen
    //            gMap.setOnPolygonClickListener {
    //                gMap.moveCamera(
    //                        CameraUpdateFactory.newLatLngBounds(
    //                                getBound(
    //                                        it.points.first(),
    //                                        it.points.last()
    //                                ), 20
    //                        )
    //                )
    ////                    gMap.animateCamera(CameraUpdateFactory.zoomOut())
    //            }
    //            // onPolygon Click move camera to keep current and clicked coordinates in screen
    //            gMap.moveCamera(
    //                    // newLatLngBounds requires LatLngBounds and value for zoom level
    //                    CameraUpdateFactory
    //                            .newLatLngBounds(
    //                                    getBound(
    //                                            it, LatLng(
    //                                                    VisitoApplication.myLocation.value!!.latitude,
    //                    VisitoApplication.myLocation.value!!.longitude
    //                                )
    //                            ), 20
    //                        )
    //                )
    //            gMap.animateCamera(CameraUpdateFactory.zoomOut())
    //        }
    //    }
    //    fun getRadius(latLng1: LatLng, latLng2: LatLng): Double {
    //        return getDistance(latLng1,latLng2) / 2
    //    }
    fun getRadius(latLng1: LatLng, latLng2: LatLng): Double {
        return getDistance(latLng1, latLng2)
    }

    //    fun getCenter(latLng1: LatLng, latLng2: LatLng): LatLng {
    //        // Mid Point Formula
    //        val lat = (latLng1.latitude + latLng2.latitude) / 2
    //        val lng = (latLng1.longitude + latLng2.longitude) / 2
    //        val center = LatLng(lat, lng)
    //        return center
    //    }
    fun lineMidPoint(latLng1: LatLng, latLng2: LatLng): LatLng {
        val lat: Double = (latLng1.latitude + latLng2.latitude) / 2
        val lng: Double = (latLng1.longitude + latLng2.longitude) / 2
        return LatLng(lat, lng)
    }

    //    fun getBound(latlng1: LatLng, latlng2: LatLng): LatLngBounds {
    //        val boundBuilder = LatLngBounds.builder()
    //        boundBuilder.include(latlng1).include(latlng2)
    //        //LatLng Bound Requires two LatLngs
    //        return boundBuilder.build()
    //    }
    fun getBounds(latLng1: LatLng, latLng2: LatLng): LatLngBounds {
        val builder: LatLngBounds.Builder = LatLngBounds.builder()
        builder.include(latLng1).include(latLng2)
        return builder.build()
    }

    //    fun getDistance(latLng1: LatLng, latLng2: LatLng): Double {
    //
    //        // d in kilometers =√((x_2-x_1)²+(y_2-y_1)²)
    //        val lat = latLng1.latitude - latLng2.latitude
    //        val lng = latLng1.longitude - latLng2.longitude
    //        return (lat.pow(2) + lng.pow(2)).pow(0.5)*100.0
    //    }
    fun getDistance(latLng1: LatLng, latLng2: LatLng): Double {
        val R = 6371 // Radius of the earth
        val latDistance = Math.toRadians(latLng2.latitude - latLng1.latitude)
        val lonDistance = Math.toRadians(latLng2.longitude - latLng1.longitude)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(latLng1.latitude)) * Math.cos(Math.toRadians(latLng2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters
        val height = 0.0
        distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)
        return Math.sqrt(distance)
    }
}
