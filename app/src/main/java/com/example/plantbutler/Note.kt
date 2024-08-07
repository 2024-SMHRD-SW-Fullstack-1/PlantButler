package com.example.plantbutler

data class Note(
    val id: Int? = null,
    val plantId: Int,
    val date: String,
    val content: String
)
