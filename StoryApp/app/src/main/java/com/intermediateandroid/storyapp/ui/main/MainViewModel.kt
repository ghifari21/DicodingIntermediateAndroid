package com.intermediateandroid.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getAccount() = userRepository.getAccount().asLiveData()

    fun deleteAccount() {
        viewModelScope.launch {
            userRepository.deleteAccount()
        }
    }

    fun getStories(token: String): LiveData<PagingData<Story>> =
        storyRepository.getStories(token).cachedIn(viewModelScope)
}