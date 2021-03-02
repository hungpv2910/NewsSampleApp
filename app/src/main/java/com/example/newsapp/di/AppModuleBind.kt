package com.example.newsapp.di

import com.example.newsapp.data.source.local.NewsLocalSource
import com.example.newsapp.data.source.local.NewsLocalSourceImpl
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.NewsRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class AppModuleBind {
    @Binds
    abstract fun bindNewsLocalSource(impl: NewsLocalSourceImpl): NewsLocalSource

    @Binds
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository
}