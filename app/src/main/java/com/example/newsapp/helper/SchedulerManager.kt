package com.example.newsapp.helper

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object SchedulerManager {
    val io: Scheduler = Schedulers.io()
    val ui: Scheduler = AndroidSchedulers.mainThread()
}