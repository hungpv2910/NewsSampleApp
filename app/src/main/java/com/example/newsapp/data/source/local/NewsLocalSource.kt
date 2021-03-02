package com.example.newsapp.data.source.local

import android.content.Context
import androidx.core.content.edit
import com.example.newsapp.data.NewsResponse
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import java.lang.Exception
import javax.inject.Inject

interface NewsLocalSource {
    fun getNews(): Single<NewsResponse>
    fun setNews(newResponse: NewsResponse): Completable
}

class NewsLocalSourceImpl @Inject constructor(private val context: Context) : NewsLocalSource {

    companion object {
        private const val KEY_PREF_NEWS_RESPONSE = "news_response"
    }

    private val pref by lazy {
        context.getSharedPreferences(
            "news",
            Context.MODE_PRIVATE
        )
    }

    override fun getNews(): Single<NewsResponse> {
        return Single.fromCallable {
            val newsResponseStr: String =
                pref.getString(KEY_PREF_NEWS_RESPONSE, null) ?: throw NoNewsException()
            Gson().fromJson(newsResponseStr, NewsResponse::class.java)
        }
    }

    override fun setNews(newResponse: NewsResponse): Completable {
        return Completable.fromCallable {
            val newsResponseStr = Gson().toJson(newResponse)
            pref.edit(true) {
                putString(KEY_PREF_NEWS_RESPONSE, newsResponseStr)
            }
        }
    }

    class NoNewsException : Exception()
}