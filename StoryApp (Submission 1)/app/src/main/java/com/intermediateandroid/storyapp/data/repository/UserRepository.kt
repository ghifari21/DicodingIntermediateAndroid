package com.intermediateandroid.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.intermediateandroid.storyapp.data.local.datastore.AccountPreferences
import com.intermediateandroid.storyapp.data.model.User
import com.intermediateandroid.storyapp.data.remote.response.LoginResponse
import com.intermediateandroid.storyapp.data.remote.response.RegisterResponse
import com.intermediateandroid.storyapp.data.remote.retrofit.ApiService
import com.intermediateandroid.storyapp.utils.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
    private val preferences: AccountPreferences
) {
    private val loginResult = MediatorLiveData<Result<User>>()
    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()

    fun login(email: String, password: String): LiveData<Result<User>> {
        loginResult.value = Result.Loading

        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        val user = User(
                            responseBody.user.userId,
                            responseBody.user.name,
                            responseBody.user.token
                        )
                        loginResult.value = Result.Success(user)
                    } else {
                        Log.d("ERROR", "onResponse: ${responseBody.message}")
                        loginResult.value = Result.Error(responseBody.message)
                    }
                } else {
                    Log.d("ERROR", "onResponse: ${response.message()}")
                    loginResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(t.message.toString())
            }
        })

        return loginResult
    }

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading

        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        registerResult.value = Result.Success(responseBody)
                    } else {
                        registerResult.value = Result.Error(responseBody.message)
                    }
                } else {
                    registerResult.value = Result.Error(response.message().toString())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(t.message.toString())
            }
        })

        return registerResult
    }


    suspend fun saveAccount(user: User) {
        preferences.saveAccount(user)
    }

    fun getAccount() = preferences.getAccount()

    suspend fun deleteAccount() {
        preferences.deleteAccount()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            preferences: AccountPreferences
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, preferences)
            }.also {
                instance = it
            }
    }
}