package com.horux.visito.operations.business_logic

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task

class FusedLocation private constructor() {
    var currentLocation: MutableLiveData<Location> = MutableLiveData()
    var request: LocationRequest = LocationRequest
        .create()
        .setInterval(10000)
        .setFastestInterval(5000)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location: Location? = locationResult.locations.firstOrNull()
            Log.e("LocationUpdate", location.toString())
            if (location != null) {
                currentLocation.setValue(location)
            }
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
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
        fusedLocationClient?.getLastLocation()
            ?.addOnSuccessListener(object : OnSuccessListener<Location?> {
                override fun onSuccess(location: Location?) {
                    if (location != null) {
                        currentLocation.setValue(location)
                    }
                }
            })
    }

    fun startLocationUpdates(activity: Activity) {
        createClient(activity.baseContext)
        task = LocationServices.getSettingsClient(activity)
            .checkLocationSettings(LocationSettingsRequest.Builder().addLocationRequest(request).build())
        task?.addOnSuccessListener(object : OnSuccessListener<LocationSettingsResponse?> {
            override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
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
                fusedLocationClient?.requestLocationUpdates(
                    request,
                    locationCallback,
                    activity.mainLooper
                )
                return
            }
        })
        task?.addOnFailureListener(object : OnFailureListener {
            override fun onFailure(exception: Exception) {
                if (exception is ResolvableApiException) {
                    try {
                        (exception as ResolvableApiException).startResolutionForResult(
                            activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    companion object {
        private var fusedLocation: FusedLocation? = null
        val instance: FusedLocation
            get() {
                if (fusedLocation == null) fusedLocation = FusedLocation()
                return fusedLocation!!
            }
    }
}
