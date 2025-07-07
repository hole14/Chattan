package com.example.chattan.repository

import com.example.chattan.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MessageRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun sendMessage(chatId: String, message: Message, onResult: (Boolean) -> Unit) {
        val msgRef = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()

        val messageWithId = message.copy(id = msgRef.id)

        msgRef.set(messageWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun listenMessages(chatId: String, onResult: (List<Message>) -> Unit): ListenerRegistration {
        return firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener{ snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                } ?: emptyList()
                onResult(list)
            }
    }
}