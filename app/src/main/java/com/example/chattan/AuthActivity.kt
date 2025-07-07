package com.example.chattan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chattan.factory.AuthFactory
import com.example.chattan.repository.AuthRepository
import com.example.chattan.viewModel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val repository = AuthRepository()
        val factory = AuthFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        val title: TextView = findViewById(R.id.titleLogin)
        val etUsername: EditText = findViewById(R.id.edt_username)
        val etEmail: EditText = findViewById(R.id.edt_email)
        val etPassword: EditText = findViewById(R.id.edt_password)
        val btnLoginTab: Button = findViewById(R.id.btnTabLogin)
        val btnRegisterTab: Button = findViewById(R.id.btnTabRegister)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)

        var isLogin = true

        btnLoginTab.setOnClickListener {
            isLogin = true
            title.text = getString(R.string.login)
            btnSubmit.text = getString(R.string.login)
            etUsername.visibility = View.GONE
            btnLoginTab.setBackgroundColor(ContextCompat.getColor(this, R.color.biru))
            btnRegisterTab.setBackgroundColor(Color.TRANSPARENT)
        }
        btnRegisterTab.setOnClickListener {
            isLogin = false
            title.text = getString(R.string.register)
            btnSubmit.text = getString(R.string.register)
            etUsername.visibility = View.VISIBLE
            btnLoginTab.setBackgroundColor(Color.TRANSPARENT)
            btnRegisterTab.setBackgroundColor(ContextCompat.getColor(this, R.color.biru))
        }

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if(isLogin) {
                if (email.isEmpty()) {
                    etEmail.error = "Email harus diisi"
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    etPassword.error = "Password harus diisi"
                }
                if (password.length < 6) {
                    etPassword.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }
                viewModel.login(email, password)
            } else {
                val username = etUsername.text.toString().trim()

                if (username.isEmpty()){
                    etUsername.error = "Username harus diisi"
                    return@setOnClickListener
                }
                if (email.isEmpty()) {
                    etEmail.error = "Email harus diisi"
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    etPassword.error = "Password harus diisi"
                }
                if (password.length < 6) {
                    etPassword.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }
                viewModel.register(username, email, password)
            }
        }
        viewModel.authResult.observe(this) { result ->
            if (result.first) {
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AvatarActivity::class.java)
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