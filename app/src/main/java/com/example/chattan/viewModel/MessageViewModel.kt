package com.example.chattan.viewModel

import android.location.GnssAntennaInfo.Listener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chattan.model.Message
import com.example.chattan.repository.MessageRepository
import com.google.firebase.firestore.ListenerRegistration

class MessageViewModel(private val repo: MessageRepository): ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private var listener: ListenerRegistration? = null

    fun sendMessage(chatId: String, senderId: String, text: String) {
        val msg = Message(
            senderId = senderId,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        repo.sendMessage(chatId, msg) { }
    }
    fun startListening(chatId: String) {
        listener?.remove()
        listener = repo.listenMessages(chatId) {
            _messages.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}