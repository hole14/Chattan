package com.example.chattan

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattan.adapter.MessageAdapter
import com.example.chattan.factory.MessageFactory
import com.example.chattan.repository.MessageRepository
import com.example.chattan.viewModel.MessageViewModel
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {
    private lateinit var viewModel: MessageViewModel
    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtMessage: EditText
    private lateinit var btnSend: ImageView

    private lateinit var chatId: String
    private lateinit var senderId: String
    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatId = intent.getStringExtra("chatsId") ?: ""
        receiverId = intent.getStringExtra("receiverId") ?: ""
        senderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        recyclerView = findViewById(R.id.rvChat)
        edtMessage = findViewById(R.id.edtChat)
        btnSend = findViewById(R.id.imgSend)

        adapter = MessageAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        val factory = MessageFactory(MessageRepository())
        viewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        viewModel.startListening(chatId)

        viewModel.messages.observe(this) { message ->
            adapter.updateMessages(message)
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }

        btnSend.setOnClickListener{
            val text = edtMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.sendMessage(chatId = chatId, senderId = senderId, text = text)
                edtMessage.text.clear()
            }
        }
    }
}