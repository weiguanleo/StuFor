package com.example.stufor.model

import android.text.Editable

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val following: MutableList<String> = mutableListOf(),
    val bio: String = "",
    val imageUrl: String = ""
)
