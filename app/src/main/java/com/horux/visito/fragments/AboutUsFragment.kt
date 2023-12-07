package com.horux.visito.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AboutUsFragment : Fragment() {
    private var binding: FragmentAboutUsBinding? = null
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAboutUsBinding.inflate(inflater)
        return binding.getRoot()
    }
}
