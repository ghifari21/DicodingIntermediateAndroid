package com.intermediateandroid.ticketingapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.intermediateandroid.ticketingapps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finishButton.setOnClickListener {
            binding.seatsView.seat?.let {
                Toast.makeText(
                    this,
                    "Kursi Anda nomor ${it.name}.",
                    Toast.LENGTH_SHORT
                ).show()
            } ?: run {
                Toast.makeText(
                    this,
                    "Silahkan pilih kursi terlebih dahulu.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}