package com.intermediateandroid.storyapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intermediateandroid.storyapp.data.model.Story
import com.intermediateandroid.storyapp.databinding.ItemStoryBinding
import com.intermediateandroid.storyapp.ui.detail.DetailActivity
import androidx.core.util.Pair

class StoryListAdapter : ListAdapter<Story, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)

                tvItemName.text = story.name
                tvItemDescription.text = story.description
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, story.id)

                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "photo"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemDescription, "description")
                    )

                itemView.context.startActivity(intent, optionCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Story> =
            object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(oldStory: Story, newStory: Story): Boolean {
                    return oldStory.id == newStory.id
                }

                override fun areContentsTheSame(oldStory: Story, newStory: Story): Boolean {
                    return oldStory == newStory
                }
            }
    }
}