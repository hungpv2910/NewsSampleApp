package com.example.newsapp.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.*
import com.example.newsapp.R
import com.example.newsapp.base.BaseActivity
import com.example.newsapp.ext.isNetworkConnected
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : BaseActivity() {

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_URL = "KEY_URL"

        fun start(activity: Activity, title: String, url: String) {
            activity.startActivity(Intent(activity, DetailActivity::class.java).apply {
                putExtra(KEY_TITLE, title)
                putExtra(KEY_URL, url)
            })
        }
    }

    private val title by lazy { intent.getStringExtra(KEY_TITLE) ?: "" }
    private val url by lazy { intent.getStringExtra(KEY_URL) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setupView()
        loadUrl(url)
    }

    override fun setupComponent() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        pullToRefreshView.setOnRefreshListener {
            loadUrl(url)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                pullToRefreshView.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                pullToRefreshView.isRefreshing = false
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request?.hasGesture() == true) return true
                return false
            }

            override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
                return true
            }
        }
    }

    private fun loadUrl(url: String) {
        pullToRefreshView.isRefreshing = true
        webView.settings.cacheMode = when {
            isNetworkConnected() -> WebSettings.LOAD_DEFAULT
            else -> WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        webView.loadUrl(url)
    }
}