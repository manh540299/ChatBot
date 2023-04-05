package com.chatbotai.aichataiart.network.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.network.model.question_ai.ResultQuestion
import com.chatbotai.aichataiart.network.retrofit.APIInterface
import com.chatbotai.aichataiart.utils.Constants
import com.chatbotai.aichataiart.utils.Constants.GPT_TYPING
import com.chatbotai.aichataiart.utils.Constants.GPT_TYPING_ERROR
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.view.adapter.ContentConversationAdapter
import com.libbtech.antivirus.security.ChCrypto
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SendQuestionDataSource(private val apiInterface: APIInterface, private val compositeDisposable: CompositeDisposable) {
    private val listenResultQuestion: MutableLiveData<ResultQuestion> by lazy {
        MutableLiveData()
    }
    private val listenNetworkState: MutableLiveData<NetworkState> by lazy {
        MutableLiveData()
    }

    val resultQuestion: LiveData<ResultQuestion> by lazy {
        listenResultQuestion
    }
    val networkState: LiveData<NetworkState> by lazy {
        listenNetworkState
    }

    companion object {
        var timeCurrent = 0L
    }

    private var countError = 0

    fun sendQuestion(listMessage: List<String>, result: (ResultQuestion) -> Unit, timeDelay: Long = 0L) {
        val model = if (ManageSaveLocal.isSmartOrAdvance()) 2 else 1
        compositeDisposable.apply {
            listenNetworkState.postValue(NetworkState(NetworkState.State.LOADING))
            var timeDelayCache = System.currentTimeMillis() - timeCurrent
            timeDelayCache = if ((timeDelayCache / 1000) < 10L) {
                (10 - (timeDelayCache / 1000)) * 1000
            } else {
                0L
            }

            var message = listMessage.firstOrNull() ?: ""
            message = ChCrypto.aesEncrypt(message)
            for (i in listMessage.subList(1, listMessage.size)) {
                message += "|${ChCrypto.aesEncrypt(i)}"
            }
            Log.d("sendQuestion", "$message ::: $listMessage")
            add(
                apiInterface.sendQuestion(message).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).delaySubscription(timeDelay + timeDelayCache, TimeUnit.MILLISECONDS).subscribe({
                    if (!com.smarttech.smarttechlibrary.utils.ManageSaveLocal.isPurchase()) {
                        ManageSaveLocal.setCountAccessSendMessage()
                    }
                    ContentConversationAdapter.isFirstLoadChat = true
                    timeCurrent = System.currentTimeMillis()
                    result(it)
                    listenNetworkState.postValue(NetworkState(NetworkState.State.LOADED))
                    GPT_TYPING = Constants.GPT_TYPING_DEFAULT
                    countError = 0
                }, {
                    countError++
                    if (countError > 3) {
                        GPT_TYPING = GPT_TYPING_ERROR
                        timeCurrent = System.currentTimeMillis()
                    }
                    sendQuestion(listMessage, result, 3000)
                    listenNetworkState.postValue(NetworkState(NetworkState.State.FAILED, it.message))
                })
            )
        }
    }
}