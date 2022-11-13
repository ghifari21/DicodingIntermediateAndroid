package com.intermediateandroid.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.intermediateandroid.storyapp.data.model.User
import com.intermediateandroid.storyapp.data.repository.UserRepository
import com.intermediateandroid.storyapp.utils.DataDummy
import com.intermediateandroid.storyapp.utils.MainDispatcherRule
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `When User Login Should Not Null And Return Success`() {
        val dummyUser = DataDummy.generateDummyUser()
        val expectedUser = MutableLiveData<Result<User>>()
        expectedUser.value = Result.Success(dummyUser)

        Mockito.`when`(userRepository.login(EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = viewModel.login(EMAIL, PASSWORD).getOrAwaitValue()

        Mockito.verify(userRepository).login(EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertTrue(actualUser is Result.Success)
        assertEquals(dummyUser, (actualUser as Result.Success).data)
    }

    @Test
    fun `When User Login Having Network Error Should Return Error `() {
        val dummyResponse = "Error"
        val expectedResponse = MutableLiveData<Result<User>>()
        expectedResponse.value = Result.Error(dummyResponse)

        Mockito.`when`(userRepository.login(EMAIL, PASSWORD)).thenReturn(expectedResponse)

        val actualResponse = viewModel.login(EMAIL, PASSWORD).getOrAwaitValue()

        Mockito.verify(userRepository).login(EMAIL, PASSWORD)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
        assertEquals(dummyResponse, (actualResponse as Result.Error).error)
    }

    @Test
    fun `When Login Success Should Save User`() = runTest {
        val dummyUser = DataDummy.generateDummyUser()
        viewModel.saveAccount(dummyUser)
        Mockito.verify(userRepository).saveAccount(dummyUser)
    }

    private companion object {
        const val EMAIL = "name@gmail.com"
        const val PASSWORD = "password"
    }
}