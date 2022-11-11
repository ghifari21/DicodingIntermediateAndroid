package com.intermediateandroid.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.intermediateandroid.storyapp.R
import com.intermediateandroid.storyapp.data.model.User
import com.intermediateandroid.storyapp.databinding.ActivityLoginBinding
import com.intermediateandroid.storyapp.ui.main.MainActivity
import com.intermediateandroid.storyapp.ui.register.RegisterActivity
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()
            when {
                email.isEmpty() -> binding.edLoginEmail.error = getString(R.string.empty_email)
                password.isEmpty() -> binding.edLoginPassword.error =
                    getString(R.string.invalid_password)
                else -> login(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val user = it.data
                        saveAccount(user)

                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.success))
                            setMessage(getString(R.string.login_success))
                            setPositiveButton(getString(R.string.to_homepage)) { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun saveAccount(user: User) {
        viewModel.saveAccount(user)
    }
}