package com.intermediateandroid.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.intermediateandroid.storyapp.R
import com.intermediateandroid.storyapp.databinding.ActivityMainBinding
import com.intermediateandroid.storyapp.ui.adapter.StoryListAdapter
import com.intermediateandroid.storyapp.ui.addstory.AddStoryActivity
import com.intermediateandroid.storyapp.ui.login.LoginActivity
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val storyAdapter = StoryListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        loginCheck()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.deleteAccount()

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()

                true
            }
            else -> true
        }
    }

    private fun loginCheck() {
        viewModel.getAccount().observe(this) {
            if (it.token == "") {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showStories(it.token)
            }
        }
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.adapter = storyAdapter

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showStories(token: String) {
        viewModel.getStories(token).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        binding.apply {
                            rvStory.visibility = View.INVISIBLE
                            pbLoading.visibility = View.VISIBLE
                            tvLoading.visibility = View.VISIBLE
                        }
                    }
                    is Result.Success -> {
                        binding.apply {
                            rvStory.visibility = View.VISIBLE
                            pbLoading.visibility = View.GONE
                            tvLoading.visibility = View.GONE
                        }
                        storyAdapter.submitList(it.data)
                    }
                    is Result.Error -> {
                        binding.apply {
                            rvStory.visibility = View.INVISIBLE
                            pbLoading.visibility = View.GONE
                            tvLoading.visibility = View.GONE
                        }
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}