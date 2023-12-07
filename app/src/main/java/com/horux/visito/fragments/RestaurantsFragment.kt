package com.horux.visito.fragments

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

class RestaurantsFragment : Fragment() {
    private var homeActivity: HomeActivity? = null
    private var viewModel: RestaurantsViewModel? = null
    private var binding: FragmentRestaurantsBinding? = null
    private var adapter: PlacesAdapter? = null
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentRestaurantsBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this).get<RestaurantsViewModel>(RestaurantsViewModel::class.java)
        return binding.getRoot()
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity?
        adapter = PlacesAdapter(homeActivity, getLayoutInflater(), ArrayList<PlaceModel>())
        binding.recycleList.setAdapter(adapter)
        binding.nearbyRestaurants.setOnClickListener(View.OnClickListener {
            binding.nearbyRestaurants.setBackgroundResource(R.drawable.bordered_background)
            binding.popularRestaurants.setBackgroundResource(R.drawable.app_background)
            if (viewModel.restaurants.getValue() != null) adapter.updateList(getNearBy(viewModel.restaurants.getValue())) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
        binding.popularRestaurants.setOnClickListener(View.OnClickListener {
            binding.popularRestaurants.setBackgroundResource(R.drawable.bordered_background)
            binding.nearbyRestaurants.setBackgroundResource(R.drawable.app_background)
            if (viewModel.restaurants.getValue() != null) adapter.updateList(viewModel.restaurants.getValue()) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
    }

    fun onStart() {
        super.onStart()
        if (homeActivity.isInternetAvailable()) {
            viewModel.fetchRestaurants()
                .observe(getViewLifecycleOwner(), object : Observer<ArrayList<PlaceModel?>?> {
                    override fun onChanged(placeModels: ArrayList<PlaceModel?>) {
                        adapter.updateList(placeModels)
                        homeActivity.setLoaderVisibility(false)
                    }
                })
        }
    }

    private fun getNearBy(placeModels: ArrayList<PlaceModel>): ArrayList<PlaceModel> {
        val nearbyPlaces: ArrayList<PlaceModel> = ArrayList<PlaceModel>()
        val currentLocation: Location = homeActivity.fusedLocation.currentLocation.getValue()
        if (currentLocation != null) {
            val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            for (placeModel in placeModels) {
                val placeLatLng = LatLng(placeModel.getLatitude(), placeModel.getLongitude())
                val distance: Double = MapOperations.getDistance(currentLatLng, placeLatLng)
                if (distance <= 10000) nearbyPlaces.add(placeModel)
            }
        }
        Log.e("NearByPlaces", nearbyPlaces.toString())
        return nearbyPlaces
    }

    fun onStop() {
        super.onStop()
    }
}
