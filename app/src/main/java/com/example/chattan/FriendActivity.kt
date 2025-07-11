package com.example.chattan

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattan.adapter.UserAdapter
import com.example.chattan.factory.AuthFactory
import com.example.chattan.model.User
import com.example.chattan.repository.AuthRepository
import com.example.chattan.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendActivity : AppCompatActivity() {
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: AuthViewModel

    private var currentMode = UserAdapter.Mode.EXPLORE
    private var lastMessages = mutableMapOf<String, String>()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val myUid = auth.currentUser?.uid ?: ""

    private var allUsers: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        val btnExplore: Button = findViewById(R.id.btnTapExplore)
        val btnFriends: Button = findViewById(R.id.btnTapFriend)
        val recyclerView: RecyclerView = findViewById(R.id.rvFriend)
        val search: EditText = findViewById(R.id.edtSearch)

        val colorBiru = ContextCompat.getColor(this, R.color.biru)
        val colorTransparan = ContextCompat.getColor(this, android.R.color.transparent)
        btnExplore.backgroundTintList = ColorStateList.valueOf(colorBiru)
        btnFriends.backgroundTintList = ColorStateList.valueOf(colorTransparan)

        val factory = AuthFactory(AuthRepository())
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        adapter = UserAdapter(emptyList(), currentMode, onAddClick = { user ->
            viewModel.getUser(myUid) { myUser ->
                if (myUser != null && !myUser.friends.contains(user.uid)) {
                    val updatedFriends = myUser.friends.toMutableList()
                    updatedFriends.add(user.uid)
                    viewModel.updateFriends(myUid, updatedFriends)
                }
            }
        }, lastMessages = lastMessages)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnExplore.setOnClickListener {
            currentMode = UserAdapter.Mode.EXPLORE
            viewModel.getAllUsers()
            btnExplore.backgroundTintList = ColorStateList.valueOf(colorBiru)
            btnFriends.backgroundTintList = ColorStateList.valueOf(colorTransparan)
        }

        btnFriends.setOnClickListener {
            currentMode = UserAdapter.Mode.FRIENDS
            viewModel.getAllUsers()
            btnExplore.backgroundTintList = ColorStateList.valueOf(colorTransparan)
            btnFriends.backgroundTintList = ColorStateList.valueOf(colorBiru)
        }

        search.addTextChangedListener {
            filterAndUpdate(it.toString())
        }

        viewModel.getAllUsers()
        viewModel.userList.observe(this) { user ->
            allUsers = user.filter { it.uid != myUid }
            filterAndUpdate(search.text.toString())
            Log.d("DebugUserList", "Jumlah user setelah filter (tanpa saya): ${allUsers.size}")
        }
    }

    private fun filterAndUpdate(query: String) {
        val filtered = allUsers.filter { it.username.contains(query, ignoreCase = true) }

        Log.d("DebugUserList", "Jumlah user setelah search: ${filtered.size}")
        for (user in filtered) {
            Log.d("DebugUserList", "User ditemukan: ${user.username}, UID: ${user.uid}")
        }

        if (currentMode == UserAdapter.Mode.EXPLORE) {
            adapter.updateList(filtered, emptyMap())
        } else {
            val friends = filtered.filter { it.friends.contains(myUid) }
            fetchLastMessages(friends) { lastMsg ->
                lastMessages = lastMsg.toMutableMap()
                adapter.updateList(friends, lastMsg)
            }
        }
    }

    private fun fetchLastMessages(friends: List<User>, callback: (Map<String, String>) -> Unit) {
        val result = mutableMapOf<String, String>()
        var complete = 0

        if (friends.isEmpty()) {
            callback(result)
            return
        }

        for (friend in friends) {
            val chatId = if (myUid < friend.uid) "$myUid-${friend.uid}" else "${friend.uid}-$myUid"
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .limitToLast(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    val lastMessage = snapshot.documents.firstOrNull()?.getString("text") ?: ""
                    result[friend.uid] = lastMessage
                    complete++
                    if (complete == friends.size) callback(result)
                }
        }
    }
}