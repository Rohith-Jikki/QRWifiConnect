package com.example.qrwificonnect

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("/H")
    fun turnOn(@Body device:Device): Call<Device>


    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("/L")
    fun turnOff(@Body device: Device): Call<Device>

}