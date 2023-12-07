package com.horux.visito.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

class FavoritesFragment : Fragment() {
    private var homeActivity: HomeActivity? = null
    private var viewModel: FavoritesViewModel? = null
    private var binding: FragmentFavoritesBinding? = null
    private var adapter: FavoritesAdapter? = null
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentFavoritesBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<FavoritesViewModel>(FavoritesViewModel::class.java)
        return binding.getRoot()
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity?
        adapter = FavoritesAdapter(homeActivity, getLayoutInflater(), ArrayList<PlaceModel>())
        binding.favoritesList.setAdapter(adapter)
    }

    fun onStart() {
        super.onStart()
        homeActivity.startLocationUpdates()
        if (homeActivity.isInternetAvailable()) {
            viewModel.fetchFavorites(getViewLifecycleOwner())
                .observe(getViewLifecycleOwner(), object : Observer<ArrayList<PlaceModel?>?> {
                    override fun onChanged(placeModels: ArrayList<PlaceModel?>) {
                        adapter.updateList(placeModels)
                        homeActivity.setLoaderVisibility(false)
                    }
                })
        }
    }

    fun onStop() {
        super.onStop()
        homeActivity.setLoaderVisibility(false)
    }
}
