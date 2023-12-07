package com.horux.visito.activities

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.horux.visito.R
import com.horux.visito.databinding.ActivityEventDescriptionBinding
import com.horux.visito.globals.AppConstants
import com.horux.visito.loadImage
import com.horux.visito.models.dao.EventModel
import com.horux.visito.operations.ui_operations.DialogPrompt
import com.horux.visito.viewmodels.EventDescriptionViewModel

class EventDescriptionActivity : PermissionActivity() {
    private val viewModel: EventDescriptionViewModel by viewModels<EventDescriptionViewModel>()
    private val binding: ActivityEventDescriptionBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_event_description)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setOnClickListeners()
        val eventModel: EventModel = getIntent().getParcelableExtra(AppConstants.STRING_DATA)!!
        Log.e("PlaceModel", eventModel.toString())
        viewModel.event.value = eventModel
    }

    private fun setObservers() {
        viewModel.event.observe(this, Observer { event ->
            if (event != null) {
                binding.pName.setText(event.title)
                binding.pDescription.setText(event.description)
                binding.pTiming.setText("Timing: " + event.startTime + " - " + event.endTime)
                binding.pAddress.setText("Address: " + event.address!!.replace("\n", ""))
                loadImage(this@EventDescriptionActivity,event.image,binding.pImage)
                startLocationUpdates()
                val locationObserver = object : Observer<Location> {
                    override fun onChanged(location: Location) {
                        viewModel.location.setValue(location)
                        val eventLatLng = LatLng(event.latitude, event.longitude)
                        if (viewModel.getDistance().getValue() == null) {
                            viewModel
                                .setDistance(
                                    LatLng(location.latitude, location.longitude),
                                    eventLatLng,
                                    this@EventDescriptionActivity
                                )
                                .observe(this@EventDescriptionActivity, object : Observer<Float> {
                                    override fun onChanged(distance: Float) {
                                        binding.pDistance.setText("Distance: $distance km")
                                        stopLocationUpdates()
                                    }
                                })
                        }
                    }
                }
                fusedLocation.currentLocation.observe(
                    this@EventDescriptionActivity,
                    locationObserver
                )
            }
        })
    }

    private fun setOnClickListeners() {
        binding.pMap.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(this@EventDescriptionActivity, MapActivity::class.java)
            intent.putExtra(AppConstants.STRING_DATA, viewModel.event.value)
            intent.putExtra(AppConstants.STRING_TYPE, AppConstants.STRING_EVENTS)
            startActivity(intent)
        })
        binding.bookARide.setOnClickListener(View.OnClickListener {
            if (viewModel.location.value != null && viewModel.event.value != null) {
                val clientID = "q7lhyjZxFyXnaJ1ChgegTgISMqFKo8-R"
                val productID = "a1111c8c-c720-46c3-8534-2fcdd730040d"
                val pickUpLat: String = viewModel.location.value!!.latitude.toString() + ""
                val pickUpLon: String = viewModel.location.value!!.longitude.toString() + ""
                val dropOffLat: String =
                    viewModel.event.value!!.latitude.toString() + ""
                val dropOffLon: String =
                    "${viewModel.event.value!!.longitude}"
                val nativeAppIntent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://m.uber.com/ul/?" +
                                "client_id=" + clientID +
                                "&action=setPickup&pickup[latitude]=" + pickUpLat + "&pickup[longitude]=" + pickUpLon +
                                "&pickup[nickname]=UberHQ&pickup[formatted_address]=1455%20Market%20St%2C%20San%20Francisco%2C%20CA%2094103" +
                                "&dropoff[latitude]=" + dropOffLat + "&dropoff[longitude]=" + dropOffLon +
                                "&dropoff[nickname]=Coit%20Tower&dropoff[formatted_address]=1%20Telegraph%20Hill%20Blvd%2C%20San%20Francisco%2C%20CA%2094133" +
                                "&product_id=" + productID
                    )
                )
                val webAppIntent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://m.uber.com/?" +
                                "client_id=" + clientID +
                                "&action=setPickup&pickup[latitude]=" + pickUpLat + "&pickup[longitude]=" + pickUpLon +
                                "&pickup[nickname]=UberHQ&pickup[formatted_address]=1455%20Market%20St%2C%20San%20Francisco%2C%20CA%2094103" +
                                "&dropoff[latitude]=" + dropOffLat + "&dropoff[longitude]=" + dropOffLon +
                                "&dropoff[nickname]=Coit%20Tower&dropoff[formatted_address]=1%20Telegraph%20Hill%20Blvd%2C%20San%20Francisco%2C%20CA%2094133" +
                                "&product_id=" + productID
                    )
                )
                val mapDirections = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "google.navigation:" +  //                            "&origin=" + pickUpLat + "," + pickUpLon +
                                "q=" + dropOffLat + "," + dropOffLon
                    )
                )
                mapDirections.setPackage("com.google.android.apps.maps")
                //                    SessionConfiguration config = new SessionConfiguration.Builder()
//                            .setClientId("<CLIENT_ID>")
//                            .setClientSecret("<CLIENT_SECRET>")
//                            .setServerToken("<SERVER_TOKEN>")
//                            .build();
//
//                    RideParameters rideParams = new RideParameters.Builder()
//                            .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
//                            .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco, California")
//                            .setDropoffLocation(37.795079, -122.4397805, "Embarcadero", "One Embarcadero Center, San Francisco")
//                            .build();
//
//                    RideRequestDeeplink deeplink = new RideRequestDeeplink.Builder(PlaceDescriptionActivity.this)
//                            .setSessionConfiguration(config)
//                            .setRideParameters(rideParams)
//                            .build();
//
//                    deeplink.execute();
                if (nativeAppIntent.resolveActivity(getPackageManager()) != null) startActivity(
                    nativeAppIntent
                )
                if (webAppIntent.resolveActivity(getPackageManager()) != null) startActivity(
                    webAppIntent
                )
                if (mapDirections.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapDirections)
                } else {
                    DialogPrompt().showMessage(
                        this@EventDescriptionActivity,
                        getLayoutInflater(),
                        "You don't have uber app"
                    )
                }
            }
        })
    }
}