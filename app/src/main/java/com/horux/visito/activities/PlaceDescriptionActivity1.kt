package com.horux.visito.activities

import android.R
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

class PlaceDescriptionActivity : PermissionActivity() {
    private var viewModel: PlaceDescriptionViewModel? = null
    private var binding: ActivityPlaceDescriptionBinding? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_description)
        viewModel =
            ViewModelProvider(this).get<PlaceDescriptionViewModel>(PlaceDescriptionViewModel::class.java)
        setObservers()
        setOnClickListeners()
        val placeModel: PlaceModel = getIntent().getParcelableExtra(AppConstants.STRING_DATA)
        Log.e("PlaceModel", placeModel.toString())
        viewModel.setPlace(placeModel)
    }

    private fun setObservers() {
        viewModel.rating.observe(this, object : Observer<Float?> {
            override fun onChanged(rating: Float) {
                if (viewModel.getPlace().getValue() != null) {
                    val place: PlaceModel = viewModel.getPlace().getValue()
                    if (rating > 0) {
                        viewModel.getPlace().getValue().setRating((rating + place.getRating()) / 2)
                    }
                }
            }
        })
        viewModel.isFavorite.observe(this, object : Observer<Boolean?> {
            override fun onChanged(isFavorite: Boolean) {
                Log.e("Fav", "isSuccessful $isFavorite")
                val drawable: Drawable = binding.pFavorite.getDrawable()
                if (isFavorite) {
                    DrawableCompat.setTint(
                        drawable,
                        getResources().getColor(R.color.transparent, getTheme())
                    )
                } else {
                    DrawableCompat.setTint(
                        drawable,
                        getResources().getColor(R.color.light_grey, getTheme())
                    )
                }
            }
        })
        viewModel.getPlace().observe(this, Observer<Any?> { place ->
            if (place != null) {
                viewModel.isFavorite(this@PlaceDescriptionActivity)
                binding.pName.setText(place.getTitle())
                binding.pDescription.setText(place.getDescription())
                binding.pTiming.setText("Timing: " + place.getOpeningTime() + " - " + place.getClosingTime())
                binding.pDaysOpen.setText("Days Open: " + place.getDaysopen())
                binding.pAddress.setText("Address: " + place.getAddress().replace("\n", ""))
                binding.pRating.setText(String.format("%.2f", place.getRating()) + "")
                binding.pStars.setRating(place.getRating())
                Log.e("Place", place.toString())
                if (place.getPhextension() != null && place.getPhextension() != "") {
                    binding.cardContact.setVisibility(View.VISIBLE)
                    binding.pContact.setOnClickListener(View.OnClickListener { callPhoneNumber(place.getPhextension()) })
                } else binding.cardContact.setVisibility(View.GONE)
                val options: RequestOptions = RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.img_loading)
                    .error(R.drawable.img_no_image)
                Glide.with(this@PlaceDescriptionActivity).load(place.getImage()).apply(options)
                    .into(binding.pImage)
                startLocationUpdates()
                val locationObserver: Observer<*> = object : Observer<Location?> {
                    override fun onChanged(location: Location) {
                        viewModel.location.setValue(location)
                        val placeLatLng = LatLng(place.getLatitude(), place.getLongitude())
                        if (viewModel.getDistance().getValue() == null) {
                            viewModel
                                .setDistance(
                                    LatLng(location.latitude, location.longitude),
                                    placeLatLng,
                                    this@PlaceDescriptionActivity
                                )
                                .observe(this@PlaceDescriptionActivity, object : Observer<Float?> {
                                    override fun onChanged(distance: Float) {
                                        binding.pDistance.setText("Distance: $distance km")
                                        stopLocationUpdates()
                                    }
                                })
                        }
                    }
                }
                fusedLocation.currentLocation.observe(
                    this@PlaceDescriptionActivity,
                    locationObserver
                )
            }
        })
    }

    private fun setOnClickListeners() {
        binding.pUserStars.setOnRatingBarChangeListener(object : OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
                viewModel.rating.setValue(rating)
            }
        })
        binding.bookARide.setOnClickListener(View.OnClickListener {
            if (viewModel.location.getValue() != null && viewModel.getPlace().getValue() != null) {
                val dropOffLat: String =
                    viewModel.getPlace().getValue().getLatitude().toString() + ""
                val dropOffLon: String =
                    viewModel.getPlace().getValue().getLongitude().toString() + ""
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
                viewModel.isFavorite.setValue(!viewModel.isFavorite.getValue())
            }
        })
    }

    protected fun onStart() {
        super.onStart()
        setObservers()
        viewModel.isFavorite(this@PlaceDescriptionActivity)
    }

    override fun onStop() {
        super.onStop()
        viewModel.updatePlaceRating()
        if (viewModel.isFavorite.getValue()) viewModel.addFavorite() else viewModel.removeFavorite()
    }
}