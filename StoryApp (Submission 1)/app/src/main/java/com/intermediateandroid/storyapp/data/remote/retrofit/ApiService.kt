package com.intermediateandroid.storyapp.data.remote.retrofit

import com.intermediateandroid.storyapp.data.remote.response.LoginResponse
import com.intermediateandroid.storyapp.data.remote.response.RegisterResponse
import com.intermediateandroid.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
    ): StoryResponse
}