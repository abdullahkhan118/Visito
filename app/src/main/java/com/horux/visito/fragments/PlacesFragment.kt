package com.horux.visito.fragments

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

class PlacesFragment : Fragment() {
    private var homeActivity: HomeActivity? = null
    private var viewModel: PlacesViewModel? = null
    private var binding: FragmentPlacesBinding? = null
    private var adapter: PlacesAdapter? = null
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPlacesBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<PlacesViewModel>(PlacesViewModel::class.java)
        return binding.getRoot()
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity?
        adapter = PlacesAdapter(homeActivity, getLayoutInflater(), ArrayList<PlaceModel>())
        binding.recycleList.setAdapter(adapter)
        binding.nearbyPlaces.setOnClickListener(View.OnClickListener {
            viewModel.popularSelected = false
            binding.nearbyPlaces.setBackgroundResource(R.drawable.bordered_background)
            binding.popularPlaces.setBackgroundResource(R.drawable.app_background)
            if (viewModel.places.getValue() != null) adapter.updateList(getNearBy(viewModel.places.getValue())) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
        binding.popularPlaces.setOnClickListener(View.OnClickListener {
            viewModel.popularSelected = true
            binding.popularPlaces.setBackgroundResource(R.drawable.bordered_background)
            binding.nearbyPlaces.setBackgroundResource(R.drawable.app_background)
            if (viewModel.places.getValue() != null) adapter.updateList(viewModel.places.getValue()) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
    }

    fun onStart() {
        super.onStart()
        homeActivity.startLocationUpdates()
        if (homeActivity.isInternetAvailable()) {
            viewModel.fetchPlaces()
                .observe(getViewLifecycleOwner(), object : Observer<ArrayList<PlaceModel?>?> {
                    override fun onChanged(placeModels: ArrayList<PlaceModel>) {
                        if (viewModel.popularSelected) adapter.updateList(placeModels) else adapter.updateList(
                            getNearBy(placeModels)
                        )
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
