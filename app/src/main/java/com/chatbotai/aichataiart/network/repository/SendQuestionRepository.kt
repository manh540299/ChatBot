package com.chatbotai.aichataiart.network.repository

import androidx.lifecycle.LiveData
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.network.datasource.SendQuestionDataSource
import com.chatbotai.aichataiart.network.model.question_ai.ResultQuestion
import com.chatbotai.aichataiart.network.retrofit.APIInterface
import io.reactivex.disposables.CompositeDisposable

class SendQuestionRepository(apiInterface: APIInterface, compositeDisposable: CompositeDisposable) {
    private val sendQuestionDataSource = SendQuestionDataSource(apiInterface, compositeDisposable)

    fun sendQuestion(listMessage: List<String>, result: (ResultQuestion) -> Unit) {
        sendQuestionDataSource.sendQuestion(listMessage, result)
    }

    fun resultQuestion(): LiveData<ResultQuestion> {
        return sendQuestionDataSource.resultQuestion
    }

    fun resultNetwork(): LiveData<NetworkState> {
        return sendQuestionDataSource.networkState
    }
}