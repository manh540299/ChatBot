package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.database.repository.ImageGeneratedRepository
import com.chatbotai.aichataiart.utils.Constants
import com.chatbotai.aichataiart.view.activity.StateSelect
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    var stateSelect = ObservableField(StateSelect.Select)
    private val imageGeneratedRepository: ImageGeneratedRepository by lazy {
        ImageGeneratedRepository.newInstance(application)
    }

    val isEmpty: LiveData<Boolean> by lazy {
        imageGeneratedRepository.isEmpty()
    }

    val isSelectAll: LiveData<Boolean> by lazy {
        imageGeneratedRepository.isSelectAll()
    }

    val numberItemSelected: LiveData<Int> by lazy {
        imageGeneratedRepository.numberItemSelected()
    }

    val listImageGenerated: Flow<PagingData<ImageGenerated>> by lazy {
        Pager(Constants.PAGE_CONFIG) {
            imageGeneratedRepository.loadAll()
        }.flow.cachedIn(viewModelScope)
    }

    init {
//        insertDemo()
        viewModelScope.launch(Dispatchers.IO) {
            imageGeneratedRepository.updateDeselect()
        }
    }

    fun selectClick() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isSelectAll.value == true) {
                imageGeneratedRepository.updateDeselect()
                stateSelect.set(StateSelect.Select)
                return@launch
            }
            stateSelect.get()?.let {
                when (it) {
                    StateSelect.Select -> {
                        imageGeneratedRepository.updateBeginSelect()
                        stateSelect.set(StateSelect.SelectAll)
                    }
                    StateSelect.SelectAll -> {
                        imageGeneratedRepository.updateSelect()
                        stateSelect.set(StateSelect.DeselectAll)
                    }
                    StateSelect.DeselectAll -> {
                        imageGeneratedRepository.updateDeselect()
                        stateSelect.set(StateSelect.Select)
                    }
                }
            }
        }
    }

    fun cancelSelect() {
        viewModelScope.launch(Dispatchers.IO) {
            imageGeneratedRepository.updateDeselect()
            stateSelect.set(StateSelect.Select)
        }
    }

    fun deleteItemSelected() {
        viewModelScope.launch(Dispatchers.IO) {
            imageGeneratedRepository.deleteItemSelected()
            imageGeneratedRepository.updateDeselect()
            stateSelect.set(StateSelect.Select)
        }
    }

    fun itemClick(imageGenerated: ImageGenerated, openHistory: () -> Unit) {
        stateSelect.get()?.let {
            if (it == StateSelect.Select) {
                openHistory()
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    imageGeneratedRepository.itemClick(imageGenerated.id)
                }
            }
        }
    }

    fun itemLongClick(id: Long) {
        stateSelect.get()?.let {
            if (it == StateSelect.Select) {
                viewModelScope.launch(Dispatchers.IO) {
                    imageGeneratedRepository.updateBeginSelect(id)
                    stateSelect.set(StateSelect.SelectAll)
                }
            }
        }
    }

    fun insertHistory(imageGenerated: ImageGenerated) {
        viewModelScope.launch(Dispatchers.IO) {
            imageGeneratedRepository.insert(imageGenerated)
        }
    }
}