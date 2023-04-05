package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.database.model.ContentConversation
import com.chatbotai.aichataiart.database.model.Conversation
import com.chatbotai.aichataiart.database.repository.ContentConversationRepository
import com.chatbotai.aichataiart.database.repository.ConversationRepository
import com.chatbotai.aichataiart.network.model.question_ai.ResultQuestion
import com.chatbotai.aichataiart.network.repository.SendQuestionRepository
import com.chatbotai.aichataiart.network.retrofit.RetrofitClient
import com.chatbotai.aichataiart.utils.Constants
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import com.smarttech.smarttechlibrary.utils.CommonUtils
import com.smarttech.smarttechlibrary.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DetailConversationViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private val conversationRepository: ConversationRepository by lazy {
        ConversationRepository.newInstance(application)
    }

    private val contentConversationRepository: ContentConversationRepository by lazy {
        ContentConversationRepository.newInstance(application)
    }

    private val sendQuestionRepository: SendQuestionRepository by lazy {
        SendQuestionRepository(RetrofitClient.getClientApp(), compositeDisposable)
    }

    val messageMain = ObservableField("")

    private var textSearch = ""
    private var id: Long = -1L

    fun setId(id: Long) {
        this.id = id
        checkBotNotAnswer()
    }


    val listConversations: Flow<PagingData<ContentConversation>> by lazy {
        Pager(Constants.PAGE_CONFIG) {
            contentConversationRepository.loadAll(id, textSearch)
        }.flow.cachedIn(viewModelScope)
    }

    val isTyping: LiveData<Boolean> by lazy {
        contentConversationRepository.isTyping(id)
    }

    fun search(textSearch: String) {
        this.textSearch = textSearch
    }

    val resultQuestion: LiveData<ResultQuestion> by lazy {
        sendQuestionRepository.resultQuestion()
    }

    val networkStateSendQuestion: LiveData<NetworkState> by lazy {
        sendQuestionRepository.resultNetwork()
    }


    private val listenMessageResult: SingleLiveEvent<String> by lazy {
        SingleLiveEvent()
    }
    val resultMessage: LiveData<String> by lazy {
        listenMessageResult
    }

    private fun checkBotNotAnswer() {
        viewModelScope.launch(Dispatchers.IO) {
            if (contentConversationRepository.checkHaveMessageBotNotAnswer(id)) {
                contentConversationRepository.getMessageBotNotAnswer(id)?.let {
                    sendQuestionRepository.sendQuestion(contentConversationRepository.getMessageOld(id)) { resultQuestion ->
                        contentConversationRepository.updateMessageBot(resultQuestion.message, id) {
                            listenMessageResult.postValue(it)
                        }
                    }
                }
            }

            messageMain.set(conversationRepository.messageMain(id))
        }
    }

    fun sendQuestion(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (id == -1L) {
                id = conversationRepository.insertConversation(Conversation(id = 0L, nameConversation = message, time = Calendar.getInstance().time.time))
                messageMain.set(message)
            } else {
                conversationRepository.updateTime(id)
            }
            Log.d("sendQuestion", "$id")
            contentConversationRepository.insertConversation(ContentConversation(id = 0L, isBot = false, message = message, time = Calendar.getInstance().time.time, id))
            delay(500)
            contentConversationRepository.insertConversation(ContentConversation(id = 0L, isBot = true, message = "", time = Calendar.getInstance().time.time, id))
            sendQuestionRepository.sendQuestion(contentConversationRepository.getMessageOld(id)) {
                contentConversationRepository.updateMessageBot(it.message, id) { messageResult ->
                    listenMessageResult.postValue(messageResult)
                }
            }
        }
    }

    fun updateMessageDefaultBot() {
        viewModelScope.launch(Dispatchers.IO) {
            contentConversationRepository.updateMessageBot("", id) {

            }
        }
    }

    fun deleteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            contentConversationRepository.delete()
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            contentConversationRepository.delete(id)
        }
    }

    override fun saveDataStore() {
        super.saveDataStore()
        Log.d("saveDataStore", "$id")
        savedStateHandle["id"] = id
    }

    override fun restoreData() {
        super.restoreData()
        savedStateHandle.get<Long>("id")?.let {
            id = it
        }
    }


}