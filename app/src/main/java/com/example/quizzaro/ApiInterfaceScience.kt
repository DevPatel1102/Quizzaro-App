package com.example.quizzaro

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterfaceScience {

    @GET("api.php?amount=10&category=17&difficulty=easy&type=multiple")
    fun getDataScience() : Call<QuizResponse>

}