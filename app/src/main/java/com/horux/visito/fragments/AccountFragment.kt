package com.horux.visito.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

class AccountFragment : Fragment() {
    private var homeActivity: HomeActivity? = null
    private var binding: FragmentAccountBinding? = null
    private var viewModel: AccountViewModel? = null
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)
        Log.e("Account", "Root is null " + (binding.getRoot() == null))
        return binding.getRoot()
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity?
        viewModel = ViewModelProvider(this).get<AccountViewModel>(AccountViewModel::class.java)
    }

    fun onStart() {
        super.onStart()
        Log.e("setData", "" + (homeActivity != null && viewModel.getUserModel() == null))
        if (homeActivity != null && viewModel.getUserModel() == null) {
            Log.e("getUser", "Called")
            homeActivity.viewModel.getUser()
                .observe(getViewLifecycleOwner(), Observer<Any?> { userModel ->
                    if (viewModel.getUserModel() == null) {
                        viewModel.setUserModel(userModel)
                        setData()
                    }
                })
        } else {
            setData()
        }
        setOnClickListeners()
    }

    fun onStop() {
        super.onStop()
        Log.e("UserModel", "Null " + (viewModel.getUserModel() == null))
        if (viewModel.getUserModel() != null) {
            Log.e("BindingName", binding.fullName.getText().toString())
            Log.e("BindingPassword", binding.password.getText().toString())
            Log.e("BindingPhoneNumber", binding.phoneNumber.getText().toString())
            viewModel.setData(
                binding.emailAddress.getText().toString(),
                binding.fullName.getText().toString(),
                binding.password.getText().toString(),
                binding.phoneNumber.getText().toString()
            )
        }
    }

    private fun setData() {
        binding.emailAddress.setText(viewModel.getUserModel().getEmail())
        binding.password.setText(viewModel.getUserModel().getPassword())
        binding.fullName.setText(viewModel.getUserModel().getName())
        binding.phoneNumber.setText(viewModel.getUserModel().getPhoneNumber())
    }

    private fun setOnClickListeners() {
        binding.btnSaveProfile.setOnClickListener { view -> saveProfile(view) }
    }

    private fun saveProfile(view: View) {
        viewModel.setData(
            binding.emailAddress.getText().toString(),
            binding.fullName.getText().toString(),
            binding.password.getText().toString(),
            binding.phoneNumber.getText().toString()
        )
        if (validInputs()) {
            if (homeActivity.isInternetAvailable()) {
                homeActivity.setLoaderVisibility(true)
                homeActivity.viewModel
                    .updateUser(viewModel.getUserModel())
                    .observe(getViewLifecycleOwner(), Observer<Any?> { userModel ->
                        if (userModel == viewModel.getUserModel()) {
                            homeActivity.showMessage("Profile Updated Successfully")
                        } else homeActivity.showMessage("Profile Update Failed")
                        homeActivity.setLoaderVisibility(false)
                    })
            }
        }
    }

    private fun validInputs(): Boolean {
        val isPasswordValid: Boolean =
            Validations.isPasswordValid(viewModel.getUserModel().getPassword())
        if (!isPasswordValid) {
            binding.passwordError.setText(Validations.STRING_INVALID_PASSWORD)
            binding.passwordError.setVisibility(View.VISIBLE)
        } else {
            binding.passwordError.setVisibility(View.GONE)
        }
        val isPhoneNumberValid: Boolean =
            Validations.isPhoneNumbeValid(viewModel.getUserModel().getPhoneNumber())
        if (!isPhoneNumberValid) {
            binding.phoneNumberError.setText(Validations.STRING_INVALID_PHONE_NUMBER)
            binding.phoneNumberError.setVisibility(View.VISIBLE)
        } else {
            binding.phoneNumberError.setVisibility(View.GONE)
        }
        return isPasswordValid && isPhoneNumberValid
    }
}
