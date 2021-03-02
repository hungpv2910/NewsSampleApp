package com.example.newsapp.domain.repository

import com.example.newsapp.data.NO_NEWS_RESPONSE
import com.example.newsapp.data.News
import com.example.newsapp.data.NewsResponse
import com.example.newsapp.data.source.local.NewsLocalSource
import com.example.newsapp.data.source.local.NewsLocalSourceImpl
import com.example.newsapp.data.source.remote.NewsRemoteSource
import io.reactivex.Single
import javax.inject.Inject

interface NewsRepository {
    fun getNews(): Single<List<News>>
}

class NewsRepositoryImpl @Inject constructor(
    private val newsLocalSource: NewsLocalSource,
    private val newsRemoteSource: NewsRemoteSource
) : NewsRepository {

    override fun getNews(): Single<List<News>> {
        return getRemoteNews()
            .flatMap { getLocalNews().map { it.articles ?: emptyList() } }
    }

    private fun getRemoteNews(): Single<List<News>> {
        return newsRemoteSource.getNews()
            .map {
                if (it.status == "ok") it
                else throw RemoteServerErrorException()
            }
            .onErrorResumeNext {
                if (it is RemoteServerErrorException) Single.error(it)
                else getLocalNews()
            }
            .flatMap {
                newsLocalSource.setNews(it)
                    .toSingleDefault(it.articles ?: emptyList())
            }
    }

    private fun getLocalNews(): Single<NewsResponse> {
        return newsLocalSource.getNews()
            .onErrorResumeNext {
                when (it) {
                    is NewsLocalSourceImpl.NoNewsException -> Single.just(NO_NEWS_RESPONSE)
                    else -> Single.error(it)
                }
            }
    }

    class RemoteServerErrorException : Exception()
}