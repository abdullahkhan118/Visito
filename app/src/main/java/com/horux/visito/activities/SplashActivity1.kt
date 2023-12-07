package com.horux.visito.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getWindow()
            .setStatusBarColor(ContextCompat.getColor(this.getApplicationContext(), R.color.orange))
        Handler(getMainLooper()).postDelayed(Runnable {
            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            finish()
        }, 2000)
    }
}