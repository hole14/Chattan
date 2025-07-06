package com.example.chattan.model

data class User (
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val avatar: String? = null,
    val friends: List<String> = listOf(),
)