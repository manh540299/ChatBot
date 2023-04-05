package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.chatbotai.aichataiart.database.model.Conversation
import com.chatbotai.aichataiart.database.repository.ConversationRepository
import com.chatbotai.aichataiart.utils.Constants
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConversationHistoryViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private val conversationRepository: ConversationRepository by lazy {
        ConversationRepository.newInstance(application)
    }

    val isEmptyConversation: LiveData<Boolean> by lazy {
        conversationRepository.isEmpty()
    }

    private var textSearch = ""

    fun search(textSearch: String) {
        this.textSearch = textSearch
    }

    val listConversations: Flow<PagingData<ItemConversationAdsModel>> by lazy {
        Pager(Constants.PAGE_CONFIG) {
            conversationRepository.loadAll(textSearch)
        }.flow.map {
            it.map {
                ItemConversationAdsModel.ItemConversation(it)
            }.insertSeparators { before, after ->
                when {
                    before == null -> ItemConversationAdsModel.ItemAds
                    after == null -> null
                    else -> null
                }
            }
        }.cachedIn(viewModelScope)
    }

    fun deleteConversation(id: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationRepository.delete(id)
        }
    }
}

@Keep
sealed class ItemConversationAdsModel {
    class ItemConversation(val tray: Conversation) : ItemConversationAdsModel()

    object ItemAds : ItemConversationAdsModel()
}