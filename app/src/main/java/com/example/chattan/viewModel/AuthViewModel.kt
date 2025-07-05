package com.example.chattan.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chattan.repository.AuthRepository
import com.example.chattan.utils.Resource

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

    val authState = MutableLiveData<Resource<String>>()

    fun register(username: String, email: String, password: String) {
        authState.value = Resource.Loading()
        repository.registerUser(email, password, username){ success, message ->
            if (success) {
                authState.value = Resource.Success(message)
            } else {
                authState.value = Resource.Error(message)
            }
        }
    }

    fun login(email:String, password: String) {
        authState.value = Resource.Loading()
        repository.loginUser(email, password) { success, message ->
            if (success) {
                authState.value = Resource.Success(message)
            } else {
                authState.value = Resource.Error(message)
            }
        }
    }
}