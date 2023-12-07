package com.horux.visito.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import java.util.Arrays

class SignInActivity : AppCompatActivity() {
    var callbackManager: CallbackManager = CallbackManager.Factory.create()
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var viewModel: SignInViewModel? = null
    private var binding: ActivitySigninBinding? = null

    //Facebook Login
    private val EMAIL = "email"
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        getWindow()
            .setStatusBarColor(
                ContextCompat.getColor(
                    this.getApplicationContext(),
                    R.color.purple_200
                )
            )
        setContentView(binding.getRoot())
        viewModel = ViewModelProvider(this).get<SignInViewModel>(SignInViewModel::class.java)
        if (viewModel.isLoggedIn()) {
            startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
            finish()
        }
        val gso: GoogleSignInOptions = Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    protected fun onStart() {
        super.onStart()
        binding.emailAddress.setText(viewModel.getEmail())
        binding.password.setText(viewModel.getPassword())
    }

    fun signIn(view: View?) {
        viewModel.setEmail(binding.emailAddress.getText().toString())
        viewModel.setPassword(binding.password.getText().toString())
        if (validInputs()) {
            binding.signinProgress.setVisibility(View.VISIBLE)
            viewModel
                .signIn(this)
                .observe(
                    this,
                    Observer<Any?> { authResultTask -> onSignInResponse(authResultTask) })
        }
    }

    private fun onSignInResponse(authResultTask: Task<AuthResult>?) {
        if (authResultTask != null) {
            if (authResultTask.isComplete()) showProgressBar(false)
            if (authResultTask.isSuccessful()) {
                if (authResultTask.getResult().getUser() != null) {
                    startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
                    finish()
                } else {
                    loginUnsuccessful()
                }
            } else {
                loginUnsuccessful()
            }
        } else {
            loginUnsuccessful()
        }
    }

    private fun loginUnsuccessful() {
        DialogPrompt().showMessage(
            this@SignInActivity,
            getLayoutInflater(),
            STRING_LOGIN_UNSUCCESSFUL
        )
    }

    fun signInWithGoogle(view: View?) {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signInWithFacebook(view: View?) {
        binding.loginButton.setPermissions(Arrays.asList(EMAIL))
        binding.loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult?>() {
                fun onSuccess(loginResult: LoginResult) {
                    val credential: AuthCredential =
                        FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken())
                    viewModel
                        .signInWithCredentials(this@SignInActivity, credential)
                        .observe(
                            this@SignInActivity,
                            Observer<Any?> { authResultTask -> onSignInResponse(authResultTask) })
                }

                fun onCancel() {
                    loginUnsuccessful()
                }

                fun onError(exception: FacebookException?) {
                    loginUnsuccessful()
                }
            })
        binding.loginButton.performClick()
    }

    fun signInWithTwitter(view: View?) {}
    fun forgotPassword(view: View?) {
        val forgotPasswordBinding: ForgotPasswordBinding =
            DataBindingUtil.inflate(getLayoutInflater(), R.layout.forgot_password, null, false)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(forgotPasswordBinding.getRoot())
            .show()
        forgotPasswordBinding.btnSendEmail.setOnClickListener { dialogView ->
            showProgressBar(true)
            viewModel.setEmail(forgotPasswordBinding.emailAddress.getText().toString())
            viewModel
                .forgotPassword(this)
                .observe(this, Observer<Any?> { voidTask ->
                    if (voidTask.isSuccessful()) {
                        showProgressBar(false)
                        DialogPrompt().showMessage(
                            this@SignInActivity,
                            getLayoutInflater(),
                            "Check your inbox"
                        )
                    } else {
                        showProgressBar(false)
                        DialogPrompt().showMessage(
                            this@SignInActivity,
                            getLayoutInflater(),
                            "Password reset failed!"
                        )
                    }
                })
            dialog.dismiss()
        }
    }

    fun createNewAccount(view: View?) {
        startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
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
        return isEmailValid && isPasswordValid
    }

    private fun showProgressBar(showProgressBar: Boolean) {
        if (showProgressBar) {
            binding.signinProgress.setVisibility(View.VISIBLE)
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            binding.signinProgress.setVisibility(View.GONE)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("ActivityResult", requestCode.toString() + "")
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            viewModel
                .signInWithGoogle(this@SignInActivity, account.getEmail(), account.getId())
                .observe(
                    this@SignInActivity,
                    Observer<Any?> { authResultTask -> onSignInResponse(authResultTask) })
        } catch (e: ApiException) {
            e.printStackTrace()
            loginUnsuccessful()
        }
    }

    companion object {
        private const val STRING_LOGIN_UNSUCCESSFUL = "Login Unsuccessful"

        //Google Login
        private const val RC_SIGN_IN = 110
    }
}