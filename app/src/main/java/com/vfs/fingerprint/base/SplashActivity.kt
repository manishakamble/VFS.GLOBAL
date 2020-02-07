package com.vfs.fingerprint.base

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.vfs.fingerprint.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        object : Handler() {}.postDelayed(object : Runnable {
            override fun run() {
                var intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }

    companion object {
        private val TAG = SplashActivity::class.qualifiedName
    }
}
