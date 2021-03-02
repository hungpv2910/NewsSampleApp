package com.example.newsapp.di

import android.content.Context
import com.example.newsapp.main.di.MainComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        AppModuleBind::class,
        ViewModelBuilderModule::class,
        SubComponentModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun mainComponentFactory(): MainComponent.Factory
}

@Module(
    subcomponents = [
        MainComponent::class
    ]
)
object SubComponentModule
