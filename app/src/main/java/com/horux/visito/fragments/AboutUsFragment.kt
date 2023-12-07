package com.horux.visito.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.horux.visito.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {
    private var binding: FragmentAboutUsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAboutUsBinding.inflate(inflater)
        return binding!!.getRoot()
    }
}
