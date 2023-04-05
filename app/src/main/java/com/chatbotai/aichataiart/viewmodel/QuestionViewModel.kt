package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.database.model.Question
import com.chatbotai.aichataiart.database.model.QuestionGroup
import com.chatbotai.aichataiart.database.model.QuestionGroupAndQuestion
import com.chatbotai.aichataiart.database.repository.QuestionRepository
import com.chatbotai.aichataiart.utils.Constants
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private var isFirstInsertQuestion = false

    private val questionRepository: QuestionRepository by lazy {
        QuestionRepository.newInstant(application)
    }

    val listQuestionGroupAndQuestion: Flow<PagingData<QuestionGroupAndQuestion>> by lazy {
        Pager(Constants.PAGE_CONFIG) {
            questionRepository.listQuestionGroupAndQuestion()
        }.flow.cachedIn(viewModelScope)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!questionRepository.isEmpty()) {
                return@launch
            }
            insertQuestion()
        }
    }

    private suspend fun insertQuestion() {
        withContext(Dispatchers.IO) {
            ArrayList<QuestionGroup>().apply {
                add(QuestionGroup(R.drawable.ic_explain, R.string.explain))
                add(QuestionGroup(R.drawable.ic_write, R.string.write))
                add(QuestionGroup(R.drawable.ic_translate_1, R.string.translate))
                add(QuestionGroup(R.drawable.ic_mail, R.string.write_email))
                questionRepository.insertQuestionGroup(this)
            }


            ArrayList<Question>().apply {
                add(Question(R.string.explain_1, R.string.explain, false))
                add(Question(R.string.explain_2, R.string.explain, false))
                add(Question(R.string.write_1, R.string.write, false))
                add(Question(R.string.write_2, R.string.write, false))
                add(Question(R.string.translate_1, R.string.translate, false))
                add(Question(R.string.write_email_1, R.string.write_email, false))
                questionRepository.insertQuestion(this)
            }
        }
    }

    fun updateCheck(ques: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            questionRepository.updateCheck(ques)
        }
    }

    fun refresh() {
        if (isFirstInsertQuestion) {
            return
        }
        isFirstInsertQuestion = true
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("QuestionViewModel", "refresh")
            questionRepository.delete()
            insertQuestion()
        }
    }
}