package com.example.newsapp.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.App
import com.example.newsapp.R
import com.example.newsapp.base.BaseActivity
import com.example.newsapp.data.News
import com.example.newsapp.detail.DetailActivity
import com.example.newsapp.ext.isNetworkConnected
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_news_layout.view.*
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private val viewModel by viewModels<MainViewModel> { viewModelProvider }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = getString(R.string.news)

        initView()
        doObserve()
        viewModel.loadNews(true)
    }

    override fun setupComponent() {
        App.instance.component.mainComponentFactory().create().inject(this)
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)

        rvNews.layoutManager = layoutManager
        rvNews.addItemDecoration(dividerItemDecoration)

        btnReload.setOnClickListener {
            if (isNetworkConnected()) {
                viewModel.loadNews(true)
            } else {
                Toast.makeText(this, R.string.turnOnConnection, Toast.LENGTH_LONG).show()
            }
        }

        pullToRefreshView.setOnRefreshListener {
            viewModel.loadNews(false)
        }
    }

    private fun doObserve() {
        viewModel.uiState.observe(this, {
            updateUI(it)
        })
    }

    private fun updateUI(uiState: MainViewModel.UIState) {
        // todo need refactor when the app become more complex
        when (uiState) {
            is MainViewModel.UIState.Loading -> {
                rvNews.visibility = View.GONE
                loadingBar.visibility = if (uiState.showIndicator) View.VISIBLE else View.GONE
                errorContainer.visibility = View.GONE
            }
            is MainViewModel.UIState.DisplayingNews -> {
                rvNews.visibility = View.VISIBLE
                rvNews.adapter = NewsAdapter(uiState.newsList, object : OnItemClickListener {
                    override fun onItemClick(position: Int, new: News) {
                        DetailActivity.start(this@MainActivity, new.title ?: "", new.url ?: "")
                    }
                })
                loadingBar.visibility = View.GONE
                errorContainer.visibility = View.GONE
                pullToRefreshView.isRefreshing = false
            }
            is MainViewModel.UIState.NoNews -> {
                rvNews.visibility = View.GONE
                loadingBar.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                tvErrorMessage.text = getString(uiState.messageRes)
                pullToRefreshView.isRefreshing = false
            }
            is MainViewModel.UIState.ServerError -> {
                rvNews.visibility = View.GONE
                loadingBar.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                tvErrorMessage.text = getString(uiState.messageRes)
                pullToRefreshView.isRefreshing = false
            }
        }
    }


    class NewsAdapter(
        private val newsList: List<News>,
        private val listener: OnItemClickListener?
    ) : RecyclerView.Adapter<NewsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_news_layout, parent, false)
            return NewsViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            holder.bind(newsList[position])
            holder.itemView.setOnClickListener {
                val adapterPosition = holder.adapterPosition
                listener?.onItemClick(adapterPosition, newsList[adapterPosition])
            }
        }

        override fun getItemCount(): Int = newsList.size
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imvNews = itemView.imvNews
        private val tvHeader = itemView.tvHeader
        private val tvDescription = itemView.tvDescription
        private val tvTime = itemView.tvTime

        fun bind(news: News) {
            Glide.with(imvNews).load(news.urlToImage).into(imvNews)
            tvHeader.text = news.title
            tvDescription.text =
                HtmlCompat.fromHtml(news.description ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvTime.text = news.publishedAt
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, new: News)
    }
}