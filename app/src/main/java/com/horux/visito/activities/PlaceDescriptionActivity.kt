package com.horux.visito.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import androidx.activity.viewModels
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.horux.visito.R
import com.horux.visito.databinding.ActivityPlaceDescriptionBinding
import com.horux.visito.globals.AppConstants
import com.horux.visito.loadImage
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.operations.ui_operations.DialogPrompt
import com.horux.visito.viewmodels.PlaceDescriptionViewModel

class PlaceDescriptionActivity : PermissionActivity() {
    private val viewModel: PlaceDescriptionViewModel by viewModels<PlaceDescriptionViewModel>()
    private val binding: ActivityPlaceDescriptionBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_place_description) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setOnClickListeners()
        val placeModel: PlaceModel = getIntent().getParcelableExtra(AppConstants.STRING_DATA)!!
        Log.e("PlaceModel", placeModel.toString())
        viewModel.setPlace(placeModel)
    }

    private fun setObservers() {
        viewModel.rating.observe(this, object : Observer<Float> {
            override fun onChanged(rating: Float) {
                if (viewModel.getPlace().getValue() != null) {
                    val place: PlaceModel = viewModel.getPlace().value!!
                    if (rating > 0) {
                        viewModel.getPlace().getValue()!!.rating = ((rating + rating) / 2)
                    }
                }
            }
        })
        viewModel.isFavorite.observe(this, object : Observer<Boolean> {
            override fun onChanged(isFavorite: Boolean) {
                Log.e("Fav", "isSuccessful $isFavorite")
                val drawable: Drawable = binding.pFavorite.getDrawable()
                if (isFavorite) {
                    DrawableCompat.setTint(
                        drawable,
                        resources.getColor(android.R.color.transparent, getTheme())
                    )
                } else {
                    DrawableCompat.setTint(
                        drawable,
                        resources.getColor(R.color.light_grey, getTheme())
                    )
                }
            }
        })
        viewModel.getPlace().observe(this, Observer { place ->
            if (place != null) {
                viewModel.isFavorite(this@PlaceDescriptionActivity)
                with(place) {
                    binding.pName.setText(title)
                    binding.pDescription.setText(description)
                    binding.pTiming.setText("Timing: " + openingTime + " - " + closingTime)
                    binding.pDaysOpen.setText("Days Open: " + daysopen)
                    binding.pAddress.setText("Address: " + address.replace("\n", ""))
                    binding.pRating.setText(String.format("%.2f", rating) + "")
                    binding.pStars.setRating(rating)
                    Log.e("Place", toString())
                    if (phextension != null && phextension != "") {
                        binding.cardContact.setVisibility(View.VISIBLE)
                        binding.pContact.setOnClickListener(View.OnClickListener {
                            callPhoneNumber(
                                phextension
                            )
                        })
                    } else binding.cardContact.setVisibility(View.GONE)
                    loadImage(this@PlaceDescriptionActivity, image, binding.pImage)
                    startLocationUpdates()
                    val locationObserver =
                        Observer<Location> { location ->
                            viewModel.location.setValue(location)
                            val placeLatLng = LatLng(latitude, longitude)
                            if (viewModel.getDistance().getValue() == null) {
                                viewModel
                                    .setDistance(
                                        LatLng(location.latitude, location.longitude),
                                        placeLatLng,
                                        this@PlaceDescriptionActivity
                                    )
                                    .observe(
                                        this@PlaceDescriptionActivity,
                                        object : Observer<Float> {
                                            override fun onChanged(distance: Float) {
                                                binding.pDistance.setText("Distance: $distance km")
                                                stopLocationUpdates()
                                            }
                                        })
                            }
                        }
                fusedLocation.currentLocation.observe(
                    this@PlaceDescriptionActivity,
                    locationObserver
                )}
            }
        })
    }

    private fun setOnClickListeners() {
        binding.pUserStars.setOnRatingBarChangeListener(object :
            RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
                viewModel.rating.setValue(rating)
            }
        })
        binding.bookARide.setOnClickListener(View.OnClickListener {
            if (viewModel.location.getValue() != null && viewModel.getPlace().getValue() != null) {
                val dropOffLat: String =
                    viewModel.getPlace().value!!.latitude.toString() + ""
                val dropOffLon: String =
                    viewModel.getPlace().value!!.longitude.toString() + ""
                val mapDirections = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "google.navigation:" +
                                "q=" + dropOffLat + "," + dropOffLon
                    )
                )
                mapDirections.setPackage("com.google.android.apps.maps")
                if (mapDirections.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapDirections)
                } else {
                    DialogPrompt().showMessage(
                        this@PlaceDescriptionActivity,
                        getLayoutInflater(),
                        "You don't have uber app"
                    )
                }
            }
        })
        binding.pMap.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(this@PlaceDescriptionActivity, MapActivity::class.java)
            intent.putExtra(AppConstants.STRING_DATA, viewModel.getPlace().getValue())
            intent.putExtra(AppConstants.STRING_TYPE, AppConstants.STRING_PLACES)
            startActivity(intent)
        })
        binding.pFavorite.setOnClickListener(View.OnClickListener {
            Log.e("Fav", "Clicked " + (viewModel.isFavorite.getValue() != null))
            if (viewModel.isFavorite.getValue() != null) {
                viewModel.isFavorite.setValue(!viewModel.isFavorite.getValue()!!)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        setObservers()
        viewModel.isFavorite(this@PlaceDescriptionActivity)
    }

    override fun onStop() {
        super.onStop()
        viewModel.updatePlaceRating()
        if (viewModel.isFavorite.getValue()!!) viewModel.addFavorite() else viewModel.removeFavorite()
    }
}