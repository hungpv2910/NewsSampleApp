package com.example.newsapp

import android.app.Application
import com.example.newsapp.di.AppComponent
import com.example.newsapp.di.DaggerAppComponent

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    val component by lazy { initComponent() }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private fun initComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}