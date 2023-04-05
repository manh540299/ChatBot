package com.chatbotai.aichataiart.network.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.network.model.generateImage.ResultGenerateImage
import com.chatbotai.aichataiart.network.retrofit.APIInterface
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SendGenerateImageDataSource(private val apiInterface: APIInterface, private val compositeDisposable: CompositeDisposable) {
    private val listenResultGenerateImage: MutableLiveData<ResultGenerateImage> by lazy {
        MutableLiveData()
    }
    private val listenNetworkState: MutableLiveData<NetworkState> by lazy {
        MutableLiveData()
    }

    val resultGenerateImage: LiveData<ResultGenerateImage> by lazy {
        listenResultGenerateImage
    }
    val networkState: LiveData<NetworkState> by lazy {
        listenNetworkState
    }

    fun sendGenerateImage(message: String, timeDelay: Long = 0L) {
        compositeDisposable.apply {
            listenNetworkState.postValue(NetworkState(NetworkState.State.LOADING))
            var timeDelayCache = System.currentTimeMillis() - SendQuestionDataSource.timeCurrent
            timeDelayCache = if ((timeDelayCache / 1000) < 15L) {
                (15 - (timeDelayCache / 1000)) * 1000
            } else {
                0L
            }
            Log.d("sendGenerateImage", "${timeDelay + timeDelayCache} :: $timeDelayCache")
            add(
                apiInterface.sendGenerateImage(message).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).delaySubscription(timeDelay + timeDelayCache, TimeUnit.MILLISECONDS).subscribe({
                    if (!com.smarttech.smarttechlibrary.utils.ManageSaveLocal.isPurchase()) {
                        ManageSaveLocal.setCountAccessGenerateImage()
                    }
                    SendQuestionDataSource.timeCurrent = System.currentTimeMillis()
                    listenResultGenerateImage.postValue(it)
                    listenNetworkState.postValue(NetworkState(NetworkState.State.LOADED))
                }, {
                    SendQuestionDataSource.timeCurrent = System.currentTimeMillis()
                    listenNetworkState.postValue(NetworkState(NetworkState.State.FAILED, it.message))
                })
            )
        }
    }
}