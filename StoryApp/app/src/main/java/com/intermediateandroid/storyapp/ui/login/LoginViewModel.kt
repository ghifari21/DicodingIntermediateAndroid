package com.intermediateandroid.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intermediateandroid.storyapp.data.model.User
import com.intermediateandroid.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(email: String, password: String) = userRepository.login(email, password)

    fun saveAccount(user: User) {
        viewModelScope.launch {
            userRepository.saveAccount(user)
        }
    }
}