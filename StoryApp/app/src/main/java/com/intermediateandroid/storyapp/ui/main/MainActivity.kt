package com.intermediateandroid.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.intermediateandroid.storyapp.R
import com.intermediateandroid.storyapp.databinding.ActivityMainBinding
import com.intermediateandroid.storyapp.ui.adapter.LoadingStateAdapter
import com.intermediateandroid.storyapp.ui.adapter.StoryListAdapter
import com.intermediateandroid.storyapp.ui.addstory.AddStoryActivity
import com.intermediateandroid.storyapp.ui.login.LoginActivity
import com.intermediateandroid.storyapp.ui.maps.MapsActivity
import com.intermediateandroid.storyapp.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val storyAdapter = StoryListAdapter()
    private var isFabMenuClicked = false

    private val fabExtendableClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.fab_extendable_close
        )
    }
    private val fabExtendableOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.fab_extendable_open
        )
    }

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
            else -> false
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
        supportActionBar?.title = getString(R.string.home)

        binding.apply {
            fabAddStory.scaleX = 0.8f
            fabAddStory.scaleY = 0.8f
            fabMaps.scaleX = 0.8f
            fabMaps.scaleY = 0.8f

            fabMenu.setOnClickListener {
                if (!isFabMenuClicked) {
                    fabAddStory.animate().translationY(-resources.getDimension(R.dimen.pos_y_fab_add_story))
                    fabMaps.animate().translationY(-resources.getDimension(R.dimen.pos_y_fab_map))
                    fabMenu.startAnimation(fabExtendableOpen)
                } else {
                    fabAddStory.animate().translationY(0f)
                    fabMaps.animate().translationY(0f)
                    fabMenu.startAnimation(fabExtendableClose)
                }
                isFabMenuClicked = !isFabMenuClicked
            }

            fabAddStory.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }

            fabMaps.setOnClickListener {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
    }

    private fun showStories(token: String) {
        loadingState(true)
        viewModel.getStories(token).observe(this) {
            loadingState(false)
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun loadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                rvStory.visibility = View.INVISIBLE
                pbLoading.visibility = View.VISIBLE
                tvLoading.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                rvStory.visibility = View.VISIBLE
                pbLoading.visibility = View.GONE
                tvLoading.visibility = View.GONE
            }
        }
    }
}