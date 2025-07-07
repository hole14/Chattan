package com.example.chattan.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chattan.repository.AuthRepository

class AuthViewModel(private val repository: AuthRepository): ViewModel() {
    private val _authResult = MutableLiveData<Pair<Boolean, String?>>()
    val authResult: LiveData<Pair<Boolean, String?>> = _authResult

    val selectedAvatar = MutableLiveData<String>()
    val saveAvatar = MutableLiveData<Boolean>()

    fun register(username: String, email: String, password: String) {
        repository.register(username, email, password) { success, message ->
            _authResult.value = Pair(success, message)
        }
    }

    fun login(email: String, password: String) {
        repository.login(email, password) { success, message ->
            _authResult.value = Pair(success, message)
        }
    }

    fun pickAvatar(avatar: String) {
        selectedAvatar.value = avatar
    }

    fun saveAvatar(uid: String) {
        val avatar = selectedAvatar.value ?: return
        repository.updateAvatar(uid, avatar) { success ->
            saveAvatar.postValue(success)
        }
    }



}