package com.example.quizzaro

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterfaceHistory {

    @GET("api.php?amount=10&category=23&difficulty=easy&type=multiple")
    fun getDataHistory() : Call<QuizResponse>
}