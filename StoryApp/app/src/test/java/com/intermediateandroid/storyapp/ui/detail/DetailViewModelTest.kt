package com.intermediateandroid.storyapp.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.data.repository.StoryRepository
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
class DetailViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        viewModel = DetailViewModel(storyRepository)
    }

    @Test
    fun `When Get Detail Story Should Not Null And Return Success`() {
        val dummyStory = DataDummy.generateDummyStory()
        val expectedStory = MutableLiveData<Result<Story>>()
        expectedStory.value = Result.Success(dummyStory)

        Mockito.`when`(storyRepository.getDetailStory(ID)).thenReturn(expectedStory)

        val actualStory = viewModel.getDetailStory(ID).getOrAwaitValue()

        Mockito.verify(storyRepository).getDetailStory(ID)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
        assertEquals(dummyStory, (actualStory as Result.Success).data)
    }

    @Test
    fun `When Detail Story Not Found Should Return Error`() {
        val expectedStory = MutableLiveData<Result<Story>>()
        expectedStory.value = Result.Error("Error")

        Mockito.`when`(storyRepository.getDetailStory(ID)).thenReturn(expectedStory)

        val actualStory = viewModel.getDetailStory(ID).getOrAwaitValue()

        Mockito.verify(storyRepository).getDetailStory(ID)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Error)
    }

    private companion object {
        const val ID = "id"
    }
}