package com.intermediateandroid.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MapsViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getAccount() = userRepository.getAccount().asLiveData()

    fun getStoriesWithLocation(token: String) = storyRepository.getStoriesWithLocation(token)
}