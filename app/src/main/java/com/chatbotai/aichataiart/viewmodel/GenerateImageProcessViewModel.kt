package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.network.model.generateImage.ResultGenerateImage
import com.chatbotai.aichataiart.network.repository.GenerateImageRepository
import com.chatbotai.aichataiart.network.retrofit.RetrofitClient
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel

class GenerateImageProcessViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private lateinit var message: String
    private val generateImageRepository: GenerateImageRepository by lazy {
        GenerateImageRepository(RetrofitClient.getClientApp(), compositeDisposable)
    }

    val resultGenerateImage: LiveData<ResultGenerateImage> by lazy {
        generateImageRepository.resultGenerateImage()
    }

    val networkState: LiveData<NetworkState> by lazy {
        generateImageRepository.resultNetwork()
    }

    fun generateImage(message: String? = null) {
        if (message == null){
            if (::message.isInitialized){
                generateImageRepository.sendGenerateImage(message = this.message)
            }
        }else{
            this.message = message
            generateImageRepository.sendGenerateImage(message = this.message)
        }
    }
}