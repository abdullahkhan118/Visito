package com.horux.visito.operations.business_logic

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException

class FusedLocation private constructor() {
    var currentLocation: MutableLiveData<Location> = MutableLiveData<Any?>()
    var request: LocationRequest = LocationRequest
        .create()
        .setInterval(10000)
        .setFastestInterval(5000)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    var locationCallback: LocationCallback = object : LocationCallback() {
        fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location: Location = locationResult.getLocations().get(0)
            Log.e("LocationUpdate", location.toString())
            if (location != null) {
                currentLocation.setValue(location)
            }
        }

        fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
        }
    }
    private val REQUEST_CHECK_SETTINGS = 100
    private var task: Task<LocationSettingsResponse>? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private fun createClient(context: Context) {
        if (fusedLocationClient != null) return
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun getLastLocation(activity: Activity) {
        createClient(activity.baseContext)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(object : OnSuccessListener<Location?>() {
                fun onSuccess(location: Location?) {
                    if (location != null) {
                        currentLocation.setValue(location)
                    }
                }
            })
    }

    fun startLocationUpdates(activity: Activity) {
        createClient(activity.baseContext)
        task = LocationServices.getSettingsClient(activity)
            .checkLocationSettings(Builder().addLocationRequest(request).build())
        task.addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse?>() {
            fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("PermissionGranted", "False")
                    return
                }
                Log.e("RequestingLocation", "True")
                fusedLocationClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    activity.mainLooper
                )
                return
            }
        })
        task.addOnFailureListener(object : OnFailureListener() {
            fun onFailure(exception: Exception) {
                if (exception is ResolvableApiException) {
                    try {
                        (exception as ResolvableApiException).startResolutionForResult(
                            activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    fun stopLocationUpdates() {
        if (fusedLocationClient != null) fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private var fusedLocation: FusedLocation? = null
        val instance: FusedLocation?
            get() {
                if (fusedLocation == null) fusedLocation = FusedLocation()
                return fusedLocation
            }
    }
}
