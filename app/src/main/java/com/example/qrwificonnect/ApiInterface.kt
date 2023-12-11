package com.example.qrwificonnect

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("/L")
    fun getData(@Field("DeviceName")DeviceName:String, @Field("SSID")SSID:String, @Field("Password")Password:String): Call<Device>

}