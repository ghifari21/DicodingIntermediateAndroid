package com.intermediateandroid.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.*
import com.intermediateandroid.storyapp.data.local.db.StoryDao
import com.intermediateandroid.storyapp.data.local.db.StoryDatabase
import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.data.remote.response.StoryResponse
import com.intermediateandroid.storyapp.data.remote.retrofit.ApiService
import com.intermediateandroid.storyapp.data.remotemediator.StoryRemoteMediator
import com.intermediateandroid.storyapp.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val database: StoryDatabase,
    private val storyDao: StoryDao
) {
    private val uploadResult = MediatorLiveData<Result<StoryResponse>>()
    private val storiesWithLocation = MediatorLiveData<Result<List<Story>>>()

    fun getStories(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(token, database, apiService),
            pagingSourceFactory = {
                storyDao.getStories()
            }
        ).liveData
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<List<Story>>> {
        storiesWithLocation.value = Result.Loading

        val client = apiService.getStoriesWithLocation("Bearer $token", 50, true)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        storiesWithLocation.value = Result.Success(responseBody.listStory!!)
                    } else {
                        storiesWithLocation.value = Result.Error(responseBody!!.message)
                    }
                } else {
                    storiesWithLocation.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                storiesWithLocation.value = Result.Error(t.message.toString())
            }
        })

        return storiesWithLocation
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
            database: StoryDatabase,
            storyDao: StoryDao
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, database, storyDao)
            }.also {
                instance = it
            }
    }
}