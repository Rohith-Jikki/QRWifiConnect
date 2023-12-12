package com.example.qrwificonnect

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private lateinit var retrofit: Retrofit
        fun getRetrofitInstance(): Retrofit {

            retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.117.29:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit


        }
    }
}