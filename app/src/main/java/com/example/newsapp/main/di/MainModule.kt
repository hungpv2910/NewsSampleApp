package com.example.newsapp.main.di

import androidx.lifecycle.ViewModel
import com.example.newsapp.di.ViewModelKey
import com.example.newsapp.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindViewModel(viewModel: MainViewModel): ViewModel
}