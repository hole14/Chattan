package com.example.chattan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.chattan.repository.AuthRepository
import com.example.chattan.viewModel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val repository = AuthRepository()
        viewModel = AuthViewModel(repository)

        val title: TextView = findViewById(R.id.titleLogin)
        val etUsername: TextView = findViewById(R.id.edt_username)
        val etEmail: TextView = findViewById(R.id.edt_email)
        val etPassword: TextView = findViewById(R.id.edt_password)
        val btnLoginTab: Button = findViewById(R.id.btnTabLogin)
        val btnRegisterTab: Button = findViewById(R.id.btnTabRegister)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val progressBar = ProgressBar(this).apply { visibility = View.GONE }
        (findViewById<CardView>(R.id.card_view)).addView(progressBar)

        var isLogin = true

        btnLoginTab.setOnClickListener {
            isLogin = true
            title.text = getString(R.string.login)
            btnSubmit.text = getString(R.string.login)
            etUsername.visibility = View.GONE
        }
        btnRegisterTab.setOnClickListener {
            isLogin = false
            title.text = getString(R.string.register)
            btnSubmit.text = getString(R.string.register)
            etUsername.visibility = View.VISIBLE
        }

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if(isLogin) {
                if (email.isEmpty() || password.isEmpty()) {
                    etEmail.error = "Email harus diisi"
                    etPassword.error = "Password harus diisi"
                    return@setOnClickListener
                }
                progressBar.visibility = View.VISIBLE
                viewModel.login(email, password)
            } else {
                val username = etUsername.text.toString().trim()

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    etUsername.error = "Username harus diisi"
                    etEmail.error = "Email harus diisi"
                    etPassword.error = "Password harus diisi"
                    return@setOnClickListener
                }
                progressBar.visibility = View.VISIBLE
                viewModel.register(username, email, password)
            }
        }
        viewModel.authResult.observe(this) { result ->
            progressBar.visibility = View.GONE
            if (result.first) {
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                val errMsg = result.second ?: "Terjadi kesalahan"
                if (isLogin) {
                    if (errMsg.contains("password", true) || errMsg.contains("no user", true)) {
                        etPassword.error = "Password salah"
                        etEmail.error = "Email tidak terdaftar"
                    }
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}