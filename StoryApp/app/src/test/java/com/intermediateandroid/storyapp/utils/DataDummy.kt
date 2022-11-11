package com.intermediateandroid.storyapp.utils

import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.data.remote.response.StoryResponse

object DataDummy {
    fun generateDummyStoryResponseWithoutLocation(): StoryResponse {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                "ID$i",
                "gosty",
                "dummy testing",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            items.add(story)
        }

        return StoryResponse(
            false,
            "Stories fetched successfully",
            items
        )
    }
}