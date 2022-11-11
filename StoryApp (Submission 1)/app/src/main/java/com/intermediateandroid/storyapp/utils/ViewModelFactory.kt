package com.intermediateandroid.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository
import com.intermediateandroid.storyapp.di.Injection
import com.intermediateandroid.storyapp.ui.addstory.AddStoryViewModel
import com.intermediateandroid.storyapp.ui.detail.DetailViewModel
import com.intermediateandroid.storyapp.ui.login.LoginViewModel
import com.intermediateandroid.storyapp.ui.main.MainViewModel
import com.intermediateandroid.storyapp.ui.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(userRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(userRepository) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(userRepository, storyRepository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(storyRepository) as T
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(userRepository, storyRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "account")

        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideStoryRepository(context),
                    Injection.provideUserRepository(context.dataStore)
                )
            }.also {
                instance = it
            }
    }
}