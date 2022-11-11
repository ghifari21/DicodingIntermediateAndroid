package com.intermediateandroid.storyapp.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.intermediateandroid.storyapp.R
import com.intermediateandroid.storyapp.databinding.ActivityRegisterBinding
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            when {
                name.isEmpty() -> binding.edRegisterName.error = getString(R.string.empty_field)
                email.isEmpty() -> binding.edRegisterEmail.error = getString(R.string.empty_email)
                password.isEmpty() -> binding.edRegisterPassword.error = getString(R.string.invalid_password)
                else -> register(name, email, password)
            }
        }

        binding.btnLogin.setOnClickListener {
            finish()
        }
    }

    private fun register(name: String, email: String, password: String) {
        viewModel.register(name, email, password).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.success))
                            setMessage(getString(R.string.account_created))
                            setPositiveButton(getString(R.string.title_login)) { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}