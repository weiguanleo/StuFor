package com.example.stufor.model

data class Post(
    val text: String =  "",
    val likeList: MutableList<String> = mutableListOf(),
    val commentList: MutableList<String> = mutableListOf(),
    val author: String = "",
    val time: Long = 0L
)
