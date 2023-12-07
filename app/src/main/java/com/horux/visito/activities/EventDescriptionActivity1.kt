package com.horux.visito.activities

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.horux.visito.R
import com.horux.visito.databinding.ActivityEventDescriptionBinding
import com.horux.visito.globals.AppConstants
import com.horux.visito.models.dao.EventModel
import com.horux.visito.viewmodels.EventDescriptionViewModel

class EventDescriptionActivity : PermissionActivity() {
    private var viewModel: EventDescriptionViewModel? = null
    private var binding: ActivityEventDescriptionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_description)
        viewModel =
            ViewModelProvider(this).get<EventDescriptionViewModel>(EventDescriptionViewModel::class.java)
        setObservers()
        setOnClickListeners()
        val eventModel: EventModel = getIntent().getParcelableExtra(AppConstants.STRING_DATA)
        Log.e("PlaceModel", eventModel.toString())
        viewModel.setEvent(eventModel)
    }

    private fun setObservers() {
        viewModel.getEvent().observe(this, Observer<Any?> { event ->
            if (event != null) {
                binding.pName.setText(event.getTitle())
                binding.pDescription.setText(event.getDescription())
                binding.pTiming.setText("Timing: " + event.getStartTime() + " - " + event.getEndTime())
                binding.pAddress.setText("Address: " + event.getAddress().replace("\n", ""))
                val options: RequestOptions = RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.img_loading)
                    .error(R.drawable.img_no_image)
                Glide.with(this@EventDescriptionActivity).load(event.getImage()).apply(options)
                    .into(binding.pImage)
                startLocationUpdates()
                val locationObserver: Observer<*> = object : Observer<Location?> {
                    override fun onChanged(location: Location) {
                        viewModel.location.setValue(location)
                        val eventLatLng = LatLng(event.getLatitude(), event.getLongitude())
                        if (viewModel.getDistance().getValue() == null) {
                            viewModel
                                .setDistance(
                                    LatLng(location.latitude, location.longitude),
                                    eventLatLng,
                                    this@EventDescriptionActivity
                                )
                                .observe(this@EventDescriptionActivity, object : Observer<Float?> {
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
            intent.putExtra(AppConstants.STRING_DATA, viewModel.getEvent().getValue())
            intent.putExtra(AppConstants.STRING_TYPE, AppConstants.STRING_EVENTS)
            startActivity(intent)
        })
        binding.bookARide.setOnClickListener(View.OnClickListener {
            if (viewModel.location.getValue() != null && viewModel.getEvent().getValue() != null) {
                val clientID = "q7lhyjZxFyXnaJ1ChgegTgISMqFKo8-R"
                val productID = "a1111c8c-c720-46c3-8534-2fcdd730040d"
                val pickUpLat: String = viewModel.location.getValue().getLatitude().toString() + ""
                val pickUpLon: String = viewModel.location.getValue().getLongitude().toString() + ""
                val dropOffLat: String =
                    viewModel.getEvent().getValue().getLatitude().toString() + ""
                val dropOffLon: String =
                    viewModel.getEvent().getValue().getLongitude().toString() + ""
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