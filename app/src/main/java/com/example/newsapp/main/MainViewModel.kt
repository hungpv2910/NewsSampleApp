package com.example.newsapp.main

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.R
import com.example.newsapp.data.News
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.helper.SchedulerManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val uiState = MutableLiveData<UIState>()

    private val disposeBag = CompositeDisposable()

    fun loadNews(showIndicator: Boolean) {
        newsRepository.getNews()
            .subscribeOn(SchedulerManager.io)
            .observeOn(SchedulerManager.ui)
            .doOnSubscribe { uiState.postValue(UIState.Loading(showIndicator)) }
            .subscribe({
                uiState.postValue(
                    if (it.isEmpty()) UIState.NoNews(R.string.noNews)
                    else UIState.DisplayingNews(it)
                )
            }, {
                uiState.postValue(UIState.ServerError(R.string.serverError))
            })
            ?.let { disposeBag.add(it) }
    }

    override fun onCleared() {
        disposeBag.clear()
        super.onCleared()
    }


    sealed class UIState {
        data class Loading(val showIndicator: Boolean) : UIState()
        data class DisplayingNews(val newsList: List<News>) : UIState()
        data class NoNews(@StringRes val messageRes: Int) : UIState()
        data class ServerError(@StringRes val messageRes: Int) : UIState()
    }
}