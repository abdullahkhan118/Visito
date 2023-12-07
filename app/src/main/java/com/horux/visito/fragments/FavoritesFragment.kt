package com.horux.visito.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.horux.visito.activities.HomeActivity
import com.horux.visito.adapters.FavoritesAdapter
import com.horux.visito.databinding.FragmentFavoritesBinding
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.viewmodels.FavoritesViewModel

class FavoritesFragment : Fragment() {
    private lateinit var homeActivity: HomeActivity
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentFavoritesBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<FavoritesViewModel>(FavoritesViewModel::class.java)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity
        adapter = FavoritesAdapter(homeActivity, getLayoutInflater(), ArrayList<PlaceModel>())
        binding.favoritesList.setAdapter(adapter)
    }

    override fun onStart() {
        super.onStart()
        homeActivity.startLocationUpdates()
        if (homeActivity.isInternetAvailable) {
            viewModel.fetchFavorites(viewLifecycleOwner)
                .observe(viewLifecycleOwner) {
                    adapter.updateList(it ?: arrayListOf())
                    homeActivity.setLoaderVisibility(false)
                }
        }
    }

    override fun onStop() {
        super.onStop()
        homeActivity.setLoaderVisibility(false)
    }
}
