package com.example.stufor.model

data class User(
    val id: String = "",
    val username: String? = "",
    val email: String = "",
    val following: MutableList<String> = mutableListOf(),
    val bio: String = "",
    val imageUrl: String = ""
)
