package com.example.quizzaro

data class QuizResponse(
    val response_code: Int,
    val results: List<DataItem>
)
