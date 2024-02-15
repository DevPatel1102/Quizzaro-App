package com.example.quizzaro

data class QuizQuestion(
val question: String,
val correctAnswer: String,
val incorrectAnswers: List<String>,
val allAnswers: List<String>
)
