package com.horux.visito.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.horux.visito.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this.getApplicationContext(), R.color.orange)
        Handler(mainLooper).postDelayed(Runnable {
            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            finish()
        }, 2000)
    }
}