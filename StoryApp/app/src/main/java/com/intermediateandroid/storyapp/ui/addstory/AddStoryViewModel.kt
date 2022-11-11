package com.intermediateandroid.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) = storyRepository.uploadStory(token, file, description, lat, lon)

    fun getAccount() = userRepository.getAccount().asLiveData()
}