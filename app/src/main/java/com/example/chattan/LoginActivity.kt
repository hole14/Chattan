package com.example.chattan

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chattan.utils.Resource
import com.example.chattan.viewModel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val registerButton = findViewById<Button>(R.id.btnTabRegister)
        val loginButton = findViewById<Button>(R.id.btnTabLogin)
        val textRegister = findViewById<TextView>(R.id.tv_login)
        val usernameInput = findViewById<EditText>(R.id.edt_username)
        val emailInput = findViewById<EditText>(R.id.edt_email)
        val passwordInput = findViewById<EditText>(R.id.edt_password)
        val login = findViewById<Button>(R.id.btn_login)

        var isLogin = true

        loginButton.setOnClickListener {
            isLogin = true
            usernameInput.visibility = View.GONE
            textRegister.text = getString(R.string.login)
            login.text = getString(R.string.login)
            registerButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.ungu)
            loginButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.biru)
        }

        registerButton.setOnClickListener {
            isLogin = false
            usernameInput.visibility = View.VISIBLE
            textRegister.text = getString(R.string.register)
            login.text = getString(R.string.register)
            registerButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.biru)
            loginButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.ungu)
        }

        login.setOnClickListener {
            if (isLogin) {
                if (emailInput.text.isEmpty() || passwordInput.text.isEmpty()) {
                    Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString().trim()).matches()) {
                    emailInput.error = "Email tidak valid"
                    emailInput.requestFocus()
                } else if (passwordInput.text.length < 6) {
                    passwordInput.error = "Password minimal 6 karakter"
                    passwordInput.requestFocus()
                } else {
                    viewModel.login(emailInput.text.toString(), passwordInput.text.toString())
                }
            } else {
                if (emailInput.text.isEmpty() || passwordInput.text.isEmpty() || usernameInput.text.isEmpty()) {
                    Toast.makeText(this, "Email, password, dan username harus diisi", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString().trim()).matches()) {
                    emailInput.error = "Email tidak valid"
                    emailInput.requestFocus()
                } else if (passwordInput.text.length < 6) {
                    passwordInput.error = "Password minimal 6 karakter"
                    passwordInput.requestFocus()
                } else {
                    viewModel.register(usernameInput.text.toString(), emailInput.text.toString(), passwordInput.text.toString())
                }
            }
        }

        viewModel.authState.observe(this) { state ->
            when (state) {
                is Resource.Error -> Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                is Resource.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(this, state.data, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }
        }

    }
}