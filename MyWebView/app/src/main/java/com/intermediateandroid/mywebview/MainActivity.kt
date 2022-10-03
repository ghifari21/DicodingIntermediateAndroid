package com.intermediateandroid.mywebview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.intermediateandroid.mywebview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.apply {
            settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Web Dicoding berhasil dimuat",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    view.loadUrl("javascript:alert('Web Dicoding berhasil dimuat')")
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(
                    view: WebView,
                    url: String,
                    message: String,
                    result: JsResult
                ): Boolean {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                    result.confirm()
                    return true
                }
            }

            loadUrl("https://www.dicoding.com")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}