package com.example.quizzaro

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterfaceSports {

    @GET("api.php?amount=10&category=21&difficulty=easy&type=multiple")
    fun getDataSports() : Call<QuizResponse>
}