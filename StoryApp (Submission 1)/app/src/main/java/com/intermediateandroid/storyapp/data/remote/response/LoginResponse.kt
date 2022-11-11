package com.intermediateandroid.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.intermediateandroid.storyapp.data.model.User

data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val user: User
)