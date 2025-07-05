package com.example.chattan.repository

import com.example.chattan.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(email: String, password: String, username: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                val user = User(uid, username, email, true)
                firestore.collection("users").document(uid).set(user)
                    .addOnSuccessListener {
                        onResult(true, "User registered successfully")
                    }
                    .addOnFailureListener{
                        onResult(false, "Failed to save user")
                    }
            } else {
                onResult(false, it.exception?.message ?: "Register failed")
            }
        }
    }
    fun loginUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, "Login successful")
                } else {
                    onResult(false, it.exception?.message ?: "Login failed")
                }
            }
    }
    fun getCurrentUserId() = auth.currentUser?.uid
}