package com.intermediateandroid.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.data.model.User
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository
import com.intermediateandroid.storyapp.utils.DataDummy
import com.intermediateandroid.storyapp.utils.MainDispatcherRule
import com.intermediateandroid.storyapp.utils.getOrAwaitValue
import com.intermediateandroid.storyapp.utils.Result
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
class MapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: MapsViewModel

    @Before
    fun setup() {
        viewModel = MapsViewModel(userRepository, storyRepository)
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

    @Test
    fun `When Get Stories With Location Should Not Empty And Return Success`() {
        val dummyResponse = DataDummy.generateDummyStoryResponseWithLocation()
        val expectedResponse = MutableLiveData<Result<List<Story>>>()
        expectedResponse.value = Result.Success(dummyResponse.listStory!!)

        Mockito.`when`(storyRepository.getStoriesWithLocation(TOKEN)).thenReturn(expectedResponse)

        val actualResponse = viewModel.getStoriesWithLocation(TOKEN).getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesWithLocation(TOKEN)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
        assertEquals(dummyResponse.listStory!!.size, (actualResponse as Result.Success).data.size)
        assertEquals(dummyResponse.listStory!![0].id, actualResponse.data[0].id)
        assertNotNull(actualResponse.data[0].lat)
        assertNotNull(actualResponse.data[0].lon)
    }

    @Test
    fun `When Get Stories With Location Having Network Error Should Return Error`() {
        val expectedResponse = MutableLiveData<Result<List<Story>>>()
        expectedResponse.value = Result.Error("Error")

        Mockito.`when`(storyRepository.getStoriesWithLocation(TOKEN)).thenReturn(expectedResponse)

        val actualResponse = viewModel.getStoriesWithLocation(TOKEN).getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesWithLocation(TOKEN)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Error)
    }

    private companion object {
        const val TOKEN = "token"
    }
}