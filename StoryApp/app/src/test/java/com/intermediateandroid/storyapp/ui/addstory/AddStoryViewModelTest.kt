package com.intermediateandroid.storyapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.intermediateandroid.storyapp.data.model.User
import com.intermediateandroid.storyapp.data.remote.response.StoryResponse
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository
import com.intermediateandroid.storyapp.utils.DataDummy
import com.intermediateandroid.storyapp.utils.MainDispatcherRule
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: AddStoryViewModel

    @Before
    fun setup() {
        viewModel = AddStoryViewModel(userRepository, storyRepository)
    }

    @Test
    fun `When Upload Story Should Return Success`() {
        val file = mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val description = "ini deskripsi".toRequestBody("text/plain".toMediaType())

        val dummyResponse = DataDummy.generateDummyUploadResponse()
        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Success(dummyResponse)

        Mockito.`when`(storyRepository.uploadStory(TOKEN, imageMultipart, description, null, null))
            .thenReturn(expectedResponse)

        val actualResponse = viewModel.uploadStory(TOKEN, imageMultipart, description, null, null).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStory(TOKEN, imageMultipart, description, null, null)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `When Upload Story Having Network Problem Should Return Error`() {
        val file = mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val description = "ini deskripsi".toRequestBody("text/plain".toMediaType())

        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Error("Error")

        Mockito.`when`(storyRepository.uploadStory(TOKEN, imageMultipart, description, null, null))
            .thenReturn(expectedResponse)

        val actualResponse = viewModel.uploadStory(TOKEN, imageMultipart, description, null, null).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStory(TOKEN, imageMultipart, description, null, null)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `When Get Account Should Not Return Empty User And Return Success`() {
        val dummyUser = DataDummy.generateDummyUser()
        val expectedUser = MutableLiveData<User>()
        expectedUser.value = dummyUser

        Mockito.`when`(userRepository.getAccount()).thenReturn(expectedUser.asFlow())

        val actualUser = viewModel.getAccount().getOrAwaitValue()

        Mockito.verify(userRepository).getAccount()
        assertNotNull(actualUser)
        assertEquals(dummyUser, actualUser)
    }

    private companion object {
        const val TOKEN = "token"
    }
}