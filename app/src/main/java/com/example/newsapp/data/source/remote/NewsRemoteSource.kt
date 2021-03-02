package com.example.newsapp.data.source.remote

import com.example.newsapp.Config
import com.example.newsapp.data.NewsResponse
import io.reactivex.Single
import retrofit2.http.GET

interface NewsRemoteSource {
    @GET("v2/everything?q=bbc-sport&apiKey=${Config.NEWS_API_KEY}")
    fun getNews(): Single<NewsResponse>
}