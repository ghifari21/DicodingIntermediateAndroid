package com.intermediateandroid.storyapp.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.intermediateandroid.storyapp.R
import com.intermediateandroid.storyapp.databinding.ActivityDetailBinding
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_detail_story)

        val id = intent.getStringExtra(EXTRA_ID).toString()

        setupView(id)
    }

    private fun setupView(id: String) {
        viewModel.getDetailStory(id).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        // Do Nothing.
                    }
                    is Result.Success -> {
                        binding.apply {
                            Glide.with(this@DetailActivity)
                                .load(it.data.photoUrl)
                                .into(ivDetailPhoto)

                            tvDetailName.text = it.data.name
                            tvDetailDescription.text = it.data.description
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
    }
}