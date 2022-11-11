package com.intermediateandroid.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.intermediateandroid.storyapp.data.model.Story

data class StoryResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<Story>?
)