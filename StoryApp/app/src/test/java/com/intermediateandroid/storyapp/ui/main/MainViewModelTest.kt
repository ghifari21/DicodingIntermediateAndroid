package com.intermediateandroid.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.data.model.User
import com.intermediateandroid.storyapp.data.repository.StoryRepository
import com.intermediateandroid.storyapp.data.repository.UserRepository
import com.intermediateandroid.storyapp.ui.adapter.StoryListAdapter
import com.intermediateandroid.storyapp.utils.DataDummy
import com.intermediateandroid.storyapp.utils.MainDispatcherRule
import com.intermediateandroid.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
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
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        mainViewModel = MainViewModel(userRepository, storyRepository)
    }

    @Test
    fun `When Get Stories Should Not Null and Return Success`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse().listStory
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStories!!)
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStories(TOKEN)).thenReturn(expectedStories)

        val actualStories: PagingData<Story> = mainViewModel.getStories(TOKEN).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories, differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `When Get Account Should Not Return Empty User And Return Success`() {
        val dummyUser = DataDummy.generateDummyUser()
        val expectedUser = MutableLiveData<User>()
        expectedUser.value = dummyUser

        Mockito.`when`(userRepository.getAccount()).thenReturn(expectedUser.asFlow())

        val actualUser = mainViewModel.getAccount().getOrAwaitValue()

        Mockito.verify(userRepository).getAccount()
        assertNotNull(actualUser)
        assertEquals(dummyUser, actualUser)
        assertEquals(dummyUser.userId, actualUser.userId)
    }

    @Test
    fun `When Delete Account Should Success`() = runTest {
        mainViewModel.deleteAccount()
        Mockito.verify(userRepository).deleteAccount()
    }

    private companion object {
        const val TOKEN = "token"
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}