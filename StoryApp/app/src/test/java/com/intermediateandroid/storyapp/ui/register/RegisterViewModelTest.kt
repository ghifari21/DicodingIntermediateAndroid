package com.intermediateandroid.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.intermediateandroid.storyapp.data.remote.response.RegisterResponse
import com.intermediateandroid.storyapp.data.repository.UserRepository
import com.intermediateandroid.storyapp.utils.DataDummy
import com.intermediateandroid.storyapp.utils.MainDispatcherRule
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        viewModel = RegisterViewModel(userRepository)
    }

    @Test
    fun `When User Register Should Not Null And Return Success`() {
        val dummyResponse = DataDummy.generateDummySuccessRegisterResponse()
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Success(dummyResponse)

        Mockito.`when`(userRepository.register(NAME, EMAIL, PASSWORD)).thenReturn(expectedResponse)

        val actualResponse = viewModel.register(NAME, EMAIL, PASSWORD).getOrAwaitValue()

        Mockito.verify(userRepository).register(NAME, EMAIL, PASSWORD)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
        assertEquals(dummyResponse.message, (actualResponse as Result.Success).data.message)
    }

    @Test
    fun `When Network Error Should Return Error`() {
        val dummyResponse = DataDummy.generateDummyErrorRegisterResponse()
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Error(dummyResponse.message)

        Mockito.`when`(userRepository.register(NAME, EMAIL, PASSWORD)).thenReturn(expectedResponse)

        val actualResponse = viewModel.register(NAME, EMAIL, PASSWORD).getOrAwaitValue()

        Mockito.verify(userRepository).register(NAME, EMAIL, PASSWORD)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }

    private companion object {
        const val NAME = "name"
        const val EMAIL = "name@gmail.com"
        const val PASSWORD = "password"
    }
}