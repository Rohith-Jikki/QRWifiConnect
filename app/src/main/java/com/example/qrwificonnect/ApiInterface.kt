package com.example.qrwificonnect

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/H")
    fun turnOn(@Field("DEVN")DEVN:String, @Field("WFID")SSID:String, @Field("PASS")PASS:String): Call<Device>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/L")
    fun turnOff(@Field("DEVN")DEVN:String, @Field("WFID")SSID:String, @Field("PASS")PASS:String): Call<Device>

}