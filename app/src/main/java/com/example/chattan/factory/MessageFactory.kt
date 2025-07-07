package com.example.chattan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chattan.repository.MessageRepository
import com.example.chattan.viewModel.MessageViewModel

class MessageFactory(private val repo: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessageViewModel(repo) as T
    }
}