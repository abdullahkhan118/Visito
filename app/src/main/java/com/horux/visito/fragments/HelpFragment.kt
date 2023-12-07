package com.horux.visito.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.horux.visito.activities.HomeActivity
import com.horux.visito.databinding.FragmentHelpBinding
import com.horux.visito.globals.UserGlobals
import com.horux.visito.viewmodels.HelpViewModel

class HelpFragment : Fragment() {
    private lateinit var homeActivity: HomeActivity
    private lateinit var viewModel: HelpViewModel
    private lateinit var binding: FragmentHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHelpBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<HelpViewModel>(HelpViewModel::class.java)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity
    }

    override fun onStart() {
        super.onStart()
        binding.message.setText(viewModel.message.value!!.message)
        binding.sendMessage.setOnClickListener(View.OnClickListener {
            viewModel.message.getValue()!!.id = (UserGlobals.user!!.getUid())
            viewModel.message.getValue()!!.message = (binding.message.getText().toString())
            if (homeActivity.isInternetAvailable) {
                viewModel.sendMessage()
                    .observe(getViewLifecycleOwner(), Observer { messageModel ->
                        homeActivity.setLoaderVisibility(false)
                        if (messageModel.message == viewModel.message.getValue()!!
                                .message
                        ) {
                            homeActivity.showMessage("Your query has been sent.")
                        }
                        homeActivity.setLoaderVisibility(false)
                    })
            }
        })
    }

    override fun onStop() {
        viewModel.message.value!!.id = (UserGlobals.user!!.getUid())
        viewModel.message.value!!.message = (binding.message.getText().toString())
        super.onStop()
    }
}