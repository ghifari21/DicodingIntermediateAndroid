package com.intermediateandroid.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.intermediateandroid.storyapp.data.local.db.StoryDao
import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.data.remote.response.StoryResponse
import com.intermediateandroid.storyapp.data.remote.retrofit.ApiService
import com.intermediateandroid.storyapp.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao
) {
    private val uploadResult = MediatorLiveData<Result<StoryResponse>>()

    fun getStories(token: String): LiveData<Result<List<Story>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories("Bearer $token")
            if (!response.error) {
                val list = response.listStory
                storyDao.deleteAll()
                if (list != null) {
                    storyDao.insertStories(list)
                }
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }

        val localData: LiveData<Result<List<Story>>> = storyDao.getStories().map {
            Result.Success(it)
        }
        emitSource(localData)
    }

    fun getDetailStory(id: String): LiveData<Result<Story>> = liveData {
        emit(Result.Loading)
        try {
            val localData: LiveData<Result<Story>> = storyDao.getStory(id).map {
                Result.Success(it)
            }
            emitSource(localData)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<StoryResponse>> {
        uploadResult.value = Result.Loading

        val client = apiService.postStory("Bearer $token", file, description)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        uploadResult.value = Result.Success(responseBody)
                    } else {
                        uploadResult.value = Result.Error(responseBody!!.message)
                    }
                } else {
                    uploadResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                uploadResult.value = Result.Error(t.message.toString())
            }
        })

        return uploadResult
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDao: StoryDao
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDao)
            }.also {
                instance = it
            }
    }
}