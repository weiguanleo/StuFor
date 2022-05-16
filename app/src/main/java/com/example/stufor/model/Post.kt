package com.example.stufor.model

data class Post(
    val content: String =  "",
    val author: User = User(),
    val time: Long = 0L,
    val likeList: MutableList<String> = mutableListOf(),
)
