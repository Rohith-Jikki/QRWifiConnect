package com.example.qrwificonnect

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private lateinit var retrofit: Retrofit
        fun getRetrofitInstance(): Retrofit {

            retrofit = Retrofit.Builder()
                .baseUrl("https://19.168.10.1:80")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit


        }
    }
}