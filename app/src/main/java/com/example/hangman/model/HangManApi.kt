package com.example.hangman.model

import retrofit2.Call
import retrofit2.http.GET

interface HangManApi {
    //Aquí van los endpoints

    @GET("cm/2023-1/hangman.php")
    fun getWord(): Call<Word>

}