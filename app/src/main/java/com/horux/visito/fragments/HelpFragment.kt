package com.horux.visito.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

class HelpFragment : Fragment() {
    private var homeActivity: HomeActivity? = null
    private var viewModel: HelpViewModel? = null
    private var binding: FragmentHelpBinding? = null
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHelpBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<HelpViewModel>(HelpViewModel::class.java)
        return binding.getRoot()
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity?
    }

    fun onStart() {
        super.onStart()
        binding.message.setText(viewModel.message.getValue().getMessage())
        binding.sendMessage.setOnClickListener(View.OnClickListener {
            viewModel.message.getValue().setId(UserGlobals.user.getUid())
            viewModel.message.getValue().setMessage(binding.message.getText().toString())
            if (homeActivity.isInternetAvailable()) {
                viewModel.sendMessage()
                    .observe(getViewLifecycleOwner(), Observer<Any?> { messageModel ->
                        homeActivity.setLoaderVisibility(false)
                        if (messageModel.getMessage() == viewModel.message.getValue()
                                .getMessage()
                        ) {
                            homeActivity.showMessage("Your query has been sent.")
                        }
                        homeActivity.setLoaderVisibility(false)
                    })
            }
        })
    }

    fun onStop() {
        viewModel.message.getValue().setId(UserGlobals.user.getUid())
        viewModel.message.getValue().setMessage(binding.message.getText().toString())
        super.onStop()
    }
}