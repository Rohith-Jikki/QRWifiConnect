package com.example.qrwificonnect

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @Headers("Content-Type:application/json")
    @POST("/L")
    fun turnOff(@Field("DeviceName")DeviceName:String, @Field("SSID")SSID:String, @Field("Password")Password:String): Call<Device>

    @FormUrlEncoded
    @Headers("Content-Type:application/json")
    @POST("/H")
    fun turnOn(@Field("DeviceName")DeviceName:String, @Field("SSID")SSID:String, @Field("Password")Password:String): Call<Device>

}