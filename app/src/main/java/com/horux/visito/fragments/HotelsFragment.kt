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
import com.horux.visito.databinding.FragmentHotelsBinding
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.operations.ui_operations.MapOperations
import com.horux.visito.viewmodels.HotelsViewModel

class HotelsFragment : Fragment() {
    private lateinit var homeActivity: HomeActivity
    private lateinit var viewModel: HotelsViewModel
    private lateinit var binding: FragmentHotelsBinding
    private lateinit var adapter: PlacesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHotelsBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<HotelsViewModel>(HotelsViewModel::class.java)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity
        adapter = PlacesAdapter(homeActivity, getLayoutInflater(), ArrayList<PlaceModel>())
        binding.recycleList.setAdapter(adapter)
        binding.nearbyHotels.setOnClickListener(View.OnClickListener {
            binding.nearbyHotels.setBackgroundResource(R.drawable.bordered_background)
            binding.popularHotels.setBackgroundResource(R.drawable.app_background)
            if (viewModel.hotels.getValue() != null) adapter.updateList(getNearBy(viewModel.hotels.getValue()!!)) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
        binding.popularHotels.setOnClickListener(View.OnClickListener {
            binding.popularHotels.setBackgroundResource(R.drawable.bordered_background)
            binding.nearbyHotels.setBackgroundResource(R.drawable.app_background)
            if (viewModel.hotels.getValue() != null) adapter.updateList(viewModel.hotels.getValue()!!) else adapter.updateList(
                ArrayList<PlaceModel>()
            )
        })
    }

    override fun onStart() {
        super.onStart()
        if (homeActivity.isInternetAvailable) {
            viewModel.fetchHotels()
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
