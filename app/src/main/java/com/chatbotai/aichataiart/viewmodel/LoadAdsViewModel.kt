package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.smarttech.smarttechlibrary.utils.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class LoadAdsViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private val listenTimeLoadAds: SingleLiveEvent<Boolean> by lazy {
        SingleLiveEvent()
    }
    val timeLoadAds: LiveData<Boolean> by lazy {
        listenTimeLoadAds
    }

    init {
        compositeDisposable.apply {
            add(Observable.interval(3, TimeUnit.SECONDS).subscribe {
                listenTimeLoadAds.postValue(true)
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        compositeDisposable.dispose()
        Log.d("LoadAdsViewModel=", "onCleared()")
    }
}