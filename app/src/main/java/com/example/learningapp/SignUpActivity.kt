package com.example.learningapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRePassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLoginHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etRePassword = findViewById(R.id.etRePassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLoginHint = findViewById(R.id.tvLoginHint)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        btnSignUp.setOnClickListener {
            val u = etUsername.text.toString().trim()
            val p = etPassword.text.toString().trim()
            val rp = etRePassword.text.toString().trim()

            if (u.isEmpty() || p.isEmpty() || rp.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (p != rp) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit().putString("username", u).putString("password", p).apply()
            Toast.makeText(this, "Sign Up Successful! Please Login.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        tvLoginHint.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
