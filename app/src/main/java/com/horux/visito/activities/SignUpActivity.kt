package com.horux.visito.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.horux.visito.R
import com.horux.visito.databinding.ActivitySignupBinding
import com.horux.visito.operations.business_logic.Validations
import com.horux.visito.operations.ui_operations.DialogPrompt
import com.horux.visito.viewmodels.SignUpViewModel

class SignUpActivity : AppCompatActivity() {
    private val viewModel: SignUpViewModel by viewModels<SignUpViewModel>()
    private val binding: ActivitySignupBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_signup) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(
            this.getApplicationContext(),
            R.color.purple_200
        )
        setContentView(binding.getRoot())
    }

    override fun onStart() {
        super.onStart()
        binding.emailAddress.setText(viewModel.email)
        binding.password.setText(viewModel.password)
        binding.fullName.setText(viewModel.fullName)
        binding.phoneNumber.setText(viewModel.phoneNumber)
    }

    fun signUp(view: View?) {
        viewModel.email = (binding.emailAddress.getText().toString())
        viewModel.password = (binding.password.getText().toString())
        viewModel.fullName = (binding.fullName.getText().toString())
        viewModel.phoneNumber = (binding.phoneNumber.getText().toString())
        if (validInputs()) {
            binding.signupProgress.setVisibility(View.VISIBLE)
            viewModel
                .signUp(this)
                .observe(this, Observer { authResultTask ->
                    if (authResultTask.isComplete) binding.signupProgress.setVisibility(View.GONE)
                    if (authResultTask.isSuccessful) //                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        finish() else DialogPrompt().showMessage(
                        this@SignUpActivity,
                        getLayoutInflater(),
                        "Registration Failed!"
                    )
                })
        }
    }

    private fun validInputs(): Boolean {
        val isEmailValid: Boolean = Validations.isEmailValid(viewModel.email)
        if (!isEmailValid) {
            binding.emailError.setText(Validations.STRING_INVALID_EMAIL)
            binding.emailError.setVisibility(View.VISIBLE)
        } else {
            binding.emailError.setVisibility(View.GONE)
        }
        val isPasswordValid: Boolean = Validations.isPasswordValid(viewModel.password)
        if (!isPasswordValid) {
            binding.passwordError.setText(Validations.STRING_INVALID_PASSWORD)
            binding.passwordError.setVisibility(View.VISIBLE)
        } else {
            binding.passwordError.setVisibility(View.GONE)
        }
        val isPhoneNumberValid: Boolean = Validations.isPhoneNumbeValid(viewModel.phoneNumber)
        if (!isPhoneNumberValid) {
            binding.phoneNumberError.setText(Validations.STRING_INVALID_PHONE_NUMBER)
            binding.phoneNumberError.setVisibility(View.VISIBLE)
        } else {
            binding.phoneNumberError.setVisibility(View.GONE)
        }
        return isEmailValid && isPasswordValid && isPhoneNumberValid
    }
}