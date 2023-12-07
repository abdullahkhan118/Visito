package com.horux.visito.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class SignUpActivity : AppCompatActivity() {
    private var viewModel: SignUpViewModel? = null
    private var binding: ActivitySignupBinding? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        getWindow()
            .setStatusBarColor(
                ContextCompat.getColor(
                    this.getApplicationContext(),
                    R.color.purple_200
                )
            )
        setContentView(binding.getRoot())
        viewModel = ViewModelProvider(this).get<SignUpViewModel>(SignUpViewModel::class.java)
    }

    protected fun onStart() {
        super.onStart()
        binding.emailAddress.setText(viewModel.getEmail())
        binding.password.setText(viewModel.getPassword())
        binding.fullName.setText(viewModel.getFullName())
        binding.phoneNumber.setText(viewModel.getPhoneNumber())
    }

    fun signUp(view: View?) {
        viewModel.setEmail(binding.emailAddress.getText().toString())
        viewModel.setPassword(binding.password.getText().toString())
        viewModel.setFullName(binding.fullName.getText().toString())
        viewModel.setPhoneNumber(binding.phoneNumber.getText().toString())
        if (validInputs()) {
            binding.signupProgress.setVisibility(View.VISIBLE)
            viewModel
                .signUp(this)
                .observe(this, Observer<Any?> { authResultTask ->
                    if (authResultTask.isComplete()) binding.signupProgress.setVisibility(View.GONE)
                    if (authResultTask.isSuccessful()) //                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        finish() else DialogPrompt().showMessage(
                        this@SignUpActivity,
                        getLayoutInflater(),
                        "Registration Failed!"
                    )
                })
        }
    }

    private fun validInputs(): Boolean {
        val isEmailValid: Boolean = Validations.isEmailValid(viewModel.getEmail())
        if (!isEmailValid) {
            binding.emailError.setText(Validations.STRING_INVALID_EMAIL)
            binding.emailError.setVisibility(View.VISIBLE)
        } else {
            binding.emailError.setVisibility(View.GONE)
        }
        val isPasswordValid: Boolean = Validations.isPasswordValid(viewModel.getPassword())
        if (!isPasswordValid) {
            binding.passwordError.setText(Validations.STRING_INVALID_PASSWORD)
            binding.passwordError.setVisibility(View.VISIBLE)
        } else {
            binding.passwordError.setVisibility(View.GONE)
        }
        val isPhoneNumberValid: Boolean = Validations.isPhoneNumbeValid(viewModel.getPhoneNumber())
        if (!isPhoneNumberValid) {
            binding.phoneNumberError.setText(Validations.STRING_INVALID_PHONE_NUMBER)
            binding.phoneNumberError.setVisibility(View.VISIBLE)
        } else {
            binding.phoneNumberError.setVisibility(View.GONE)
        }
        return isEmailValid && isPasswordValid && isPhoneNumberValid
    }
}