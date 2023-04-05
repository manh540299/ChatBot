package com.chatbotai.aichataiart.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel(application: Application, private val handle: SavedStateHandle) : AndroidViewModel(application) {
    val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    /*Save data*/
    open fun saveDataStore() {

    }

    open fun restoreData() {

    }
    /**/

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}