package com.horux.visito.fragments

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.horux.visito.R
import com.horux.visito.activities.HomeActivity
import com.horux.visito.adapters.PlacesAdapter
import com.horux.visito.databinding.FragmentRestaurantsBinding
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.operations.ui_operations.MapOperations
import com.horux.visito.viewmodels.RestaurantsViewModel

class RestaurantsFragment : Fragment() {
    private lateinit var homeActivity: HomeActivity
    private lateinit var viewModel: RestaurantsViewModel
    private lateinit var binding: FragmentRestaurantsBinding
    private lateinit var adapter: PlacesAdapter
    override fun onCreateView(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity
        adapter = PlacesAdapter(homeActivity, getLayoutInflater(), ArrayList<PlaceModel>())
        binding.recycleList.setAdapter(adapter)
        binding.nearbyRestaurants.setOnClickListener(View.OnClickListener {
            binding.nearbyRestaurants.setBackgroundResource(R.drawable.bordered_background)
            binding.popularRestaurants.setBackgroundResource(R.drawable.app_background)
            if (viewModel.restaurants.getValue() != null) adapter.updateList(getNearBy(viewModel.restaurants.getValue()!!)) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
        binding.popularRestaurants.setOnClickListener(View.OnClickListener {
            binding.popularRestaurants.setBackgroundResource(R.drawable.bordered_background)
            binding.nearbyRestaurants.setBackgroundResource(R.drawable.app_background)
            if (viewModel.restaurants.getValue() != null) adapter.updateList(viewModel.restaurants.getValue()!!) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
    }

    override fun onStart() {
        super.onStart()
        if (homeActivity.isInternetAvailable) {
            viewModel.fetchRestaurants()
                .observe(getViewLifecycleOwner()
                ) { placeModels ->
                    adapter.updateList(placeModels)
                    homeActivity.setLoaderVisibility(false)
                }
        }
    }

    private fun getNearBy(placeModels: ArrayList<PlaceModel>): ArrayList<PlaceModel> {
        val nearbyPlaces: ArrayList<PlaceModel> = ArrayList<PlaceModel>()
        val currentLocation: Location = homeActivity.fusedLocation.currentLocation.getValue()
        if (currentLocation != null) {
            val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            for (placeModel in placeModels) {
                val placeLatLng = LatLng(placeModel.latitude, placeModel.longitude)
                val distance: Double = MapOperations.getDistance(currentLatLng, placeLatLng)
                if (distance <= 10000) nearbyPlaces.add(placeModel)
            }
        }
        Log.e("NearByPlaces", nearbyPlaces.toString())
        return nearbyPlaces
    }

    override fun onStop() {
        super.onStop()
    }
}
