package com.intermediateandroid.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.intermediateandroid.storyapp.data.local.datastore.AccountPreferences
import com.intermediateandroid.storyapp.data.local.db.StoryDatabase
import com.intermediateandroid.storyapp.data.remote.retrofit.ApiConfig
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        return StoryRepository.getInstance(apiService, database, dao)
    }

    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepository {
        val apiService = ApiConfig.getApiService()
        val pref = AccountPreferences.getInstance(dataStore)
        return UserRepository.getInstance(apiService, pref)
    }
}