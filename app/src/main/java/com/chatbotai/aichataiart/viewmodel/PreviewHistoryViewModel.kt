package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.database.repository.ImageGeneratedRepository
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewHistoryViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private var id = -1L
    private val imageGeneratedRepository: ImageGeneratedRepository by lazy {
        ImageGeneratedRepository.newInstance(application)
    }

    val imageGenerated: LiveData<ImageGenerated> by lazy {
        imageGeneratedRepository.getImageGenerated(id)
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun delete(action : () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            imageGeneratedRepository.delete(id)
            withContext(Dispatchers.Main){
                action()
            }
        }
    }

    suspend fun getPath(): String {
        return withContext(Dispatchers.IO) {
            imageGeneratedRepository.getPath(id)
        }
    }
}