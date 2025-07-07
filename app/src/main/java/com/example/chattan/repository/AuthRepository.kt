package com.example.chattan.repository

import com.example.chattan.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun register(
        username: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: ""
                val user = User(uid = userId, username = username, email = email)
                firestore.collection("users").document(userId).set(user)
                    .addOnSuccessListener {
                        onResult(true, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e.message)
                    }
            }.addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }.addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun updateAvatar(uid: String, avatar: String, callback: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("avatar", avatar)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                onResult(users)
            }
    }

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { result ->
                onResult(result.toObject(User::class.java))
            }
    }

    fun updateFriends(myUid: String, updateFriends: List<String>, onResult: (Boolean) -> Unit) {
        firestore.collection("users").document(myUid)
            .update("friends", updateFriends)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}