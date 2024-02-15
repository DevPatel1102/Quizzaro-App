package com.example.quizzaro

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterfaceArt {
    @GET("api.php?amount=10&category=25&difficulty=easy&type=multiple")
    fun getDataArt() : Call<QuizResponse>
}