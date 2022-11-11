package com.intermediateandroid.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import com.intermediateandroid.storyapp.data.repository.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel()  {
    fun getDetailStory(id: String) = storyRepository.getDetailStory(id)
}