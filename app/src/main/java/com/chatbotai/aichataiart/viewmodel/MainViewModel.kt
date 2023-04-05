package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.database.repository.ImageGeneratedRepository
import com.chatbotai.aichataiart.utils.Constants
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class MainViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private val imageGeneratedRepository: ImageGeneratedRepository by lazy {
        ImageGeneratedRepository.newInstance(application)
    }

    val isEmpty: LiveData<Boolean> by lazy {
        imageGeneratedRepository.isEmpty()
    }

    val listImageGenerated: Flow<PagingData<ImageGenerated>> by lazy {
        Pager(Constants.PAGE_CONFIG) {
            imageGeneratedRepository.loadPre()
        }.flow.cachedIn(viewModelScope)
    }
}