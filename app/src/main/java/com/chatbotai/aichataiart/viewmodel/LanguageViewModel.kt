package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.chatbotai.aichataiart.model.Language
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel

class LanguageViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private val listLanguage = MutableLiveData<List<Language>>()

    fun setLanguage(languages: List<Language>) {
        listLanguage.value = languages
    }

    fun getLanguage(): LiveData<List<Language>> = listLanguage
}