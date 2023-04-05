package com.chatbotai.aichataiart.network.repository

import androidx.lifecycle.LiveData
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.network.datasource.SendGenerateImageDataSource
import com.chatbotai.aichataiart.network.model.generateImage.ResultGenerateImage
import com.chatbotai.aichataiart.network.retrofit.APIInterface
import io.reactivex.disposables.CompositeDisposable

class GenerateImageRepository(apiInterface: APIInterface, compositeDisposable: CompositeDisposable) {
    private val sendGenerateImageDataSource = SendGenerateImageDataSource(apiInterface, compositeDisposable)

    fun sendGenerateImage(message: String) {
        sendGenerateImageDataSource.sendGenerateImage(message)
    }

    fun resultGenerateImage(): LiveData<ResultGenerateImage> {
        return sendGenerateImageDataSource.resultGenerateImage
    }

    fun resultNetwork(): LiveData<NetworkState> {
        return sendGenerateImageDataSource.networkState
    }
}