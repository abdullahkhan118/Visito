package com.horux.visito.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.horux.visito.R
import com.horux.visito.activities.HomeActivity
import com.horux.visito.databinding.FragmentAccountBinding
import com.horux.visito.operations.business_logic.Validations
import com.horux.visito.viewmodels.AccountViewModel

class AccountFragment : Fragment() {
    private lateinit var homeActivity: HomeActivity
    private lateinit var  binding: FragmentAccountBinding
    private lateinit var  viewModel: AccountViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity
        viewModel = ViewModelProvider(this).get<AccountViewModel>(AccountViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.getUserModel() == null) {
            Log.e("getUser", "Called")
            homeActivity.viewModel.user
                .observe(viewLifecycleOwner) { userModel ->
                    if (viewModel.getUserModel() == null) {
                        viewModel.setUserModel(userModel)
                        setData()
                    }
                }
        } else {
            setData()
        }
        setOnClickListeners()
    }

    override fun onStop() {
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
        binding.emailAddress.setText(viewModel.getUserModel()?.email)
        binding.password.setText(viewModel.getUserModel()?.password)
        binding.fullName.setText(viewModel.getUserModel()?.name)
        binding.phoneNumber.setText(viewModel.getUserModel()?.phoneNumber)
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
            if (homeActivity.isInternetAvailable) {
                homeActivity.setLoaderVisibility(true)
                viewModel.getUserModel()?.let {
                    homeActivity.viewModel
                        .updateUser(it)
                        .observe(viewLifecycleOwner) { userModel ->
                            if (userModel == viewModel.getUserModel()) {
                                homeActivity.showMessage("Profile Updated Successfully")
                            } else homeActivity.showMessage("Profile Update Failed")
                            homeActivity.setLoaderVisibility(false)
                        }
                }
            }
        }
    }

    private fun validInputs(): Boolean {
        val isPasswordValid: Boolean =
            Validations.isPasswordValid(viewModel.getUserModel()!!.password)
        if (!isPasswordValid) {
            binding.passwordError.setText(Validations.STRING_INVALID_PASSWORD)
            binding.passwordError.setVisibility(View.VISIBLE)
        } else {
            binding.passwordError.setVisibility(View.GONE)
        }
        val isPhoneNumberValid: Boolean =
            Validations.isPhoneNumbeValid(viewModel.getUserModel()!!.phoneNumber)
        if (!isPhoneNumberValid) {
            binding.phoneNumberError.setText(Validations.STRING_INVALID_PHONE_NUMBER)
            binding.phoneNumberError.setVisibility(View.VISIBLE)
        } else {
            binding.phoneNumberError.setVisibility(View.GONE)
        }
        return isPasswordValid && isPhoneNumberValid
    }
}
