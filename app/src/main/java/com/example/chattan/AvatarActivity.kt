package com.example.chattan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chattan.repository.AuthRepository
import com.example.chattan.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class AvatarActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private  var selectedAvatar: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)

        val repository = AuthRepository()
        viewModel = AuthViewModel(repository)

        val ivSelectedAvatar: ImageView = findViewById(R.id.ivSelectedAvatar)
        val gridAvatar: GridLayout = findViewById(R.id.grid_avatar)
        val btnSimpan: Button = findViewById(R.id.btnSimpan)

        val avatars = resources.getStringArray(R.array.avatar)
        btnSimpan.isEnabled = false

        avatars.forEach { avatarName ->
            val resId = resources.getIdentifier(avatarName, "drawable", packageName)
            val iv = ImageView(this)
            iv.setImageResource(resId)
            val params = GridLayout.LayoutParams()
            params.height = 120
            params.width = 120
            params.setMargins(16, 16, 16, 16)
            iv.layoutParams = params
            iv.setOnClickListener {
                selectedAvatar = avatarName
                ivSelectedAvatar.setImageResource(resId)
                btnSimpan.isEnabled = true
                viewModel.pickAvatar(avatarName)
            }
            gridAvatar.addView(iv)
        }

        btnSimpan.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && selectedAvatar != null) {
                viewModel.saveAvatar(uid)
            }
        }

        viewModel.saveAvatar.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Avatar berhasil disimpan", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FriendActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Gagal menyimpan avatar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}