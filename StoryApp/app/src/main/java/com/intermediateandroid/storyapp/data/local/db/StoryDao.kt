package com.intermediateandroid.storyapp.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.intermediateandroid.storyapp.data.model.Story

@Dao
interface StoryDao {
    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getStories(): LiveData<List<Story>>

    @Query("SELECT * FROM story WHERE id = :id LIMIT 1")
    fun getStory(id: String): LiveData<Story>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(stories: List<Story>)

    @Query("DELETE FROM story")
    suspend fun deleteAll()

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    suspend fun getSuspendStories(): List<Story>
}