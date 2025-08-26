package com.example.learningapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000 // 2 seconds
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.max = 100

        startProgressBar()
    }

    private fun startProgressBar() {
        val handler = Handler(Looper.getMainLooper())
        var progress = 0

        val runnable = object : Runnable {
            override fun run() {
                progress += 4
                if (progress > 100) progress = 100
                progressBar.progress = progress
                if (progress < 100) {
                    handler.postDelayed(this, SPLASH_DELAY / 25)
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
        handler.post(runnable)
    }
}
