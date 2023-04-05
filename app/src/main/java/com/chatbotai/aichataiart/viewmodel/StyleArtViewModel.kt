package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.model.ArtStyle
import com.chatbotai.aichataiart.model.Prompt
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import com.smarttech.smarttechlibrary.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StyleArtViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    private val promptList = listOf(
        Prompt(R.string.prompt_1),
        Prompt(R.string.prompt_2),
        Prompt(R.string.prompt_3),
        Prompt(R.string.prompt_4),
        Prompt(R.string.prompt_6),
        Prompt(R.string.prompt_7),
        Prompt(R.string.prompt_8),
        Prompt(R.string.prompt_9),
        Prompt(R.string.prompt_10),
        Prompt(R.string.prompt_11),
        Prompt(R.string.prompt_12)
    )
    private val styleArtList = listOf(
        ArtStyle(R.string.art_1, R.drawable.ic_style_art_1),
        ArtStyle(R.string.art_2, R.drawable.ic_style_art_2),
        ArtStyle(R.string.art_3, R.drawable.ic_style_art_3),
        ArtStyle(R.string.art_4, R.drawable.ic_style_art_4),
        ArtStyle(R.string.art_5, R.drawable.ic_style_art_5),
        ArtStyle(R.string.art_6, R.drawable.ic_style_art_6),
        ArtStyle(R.string.art_7, R.drawable.ic_style_art_7),
        ArtStyle(R.string.art_8, R.drawable.ic_style_art_8),
        ArtStyle(R.string.art_9, R.drawable.ic_style_art_9),
        ArtStyle(R.string.art_10, R.drawable.ic_style_art_10),
        ArtStyle(R.string.art_11, R.drawable.ic_style_art_11),
        ArtStyle(R.string.art_12, R.drawable.ic_style_art_12),
        ArtStyle(R.string.art_13, R.drawable.ic_style_art_13),
        ArtStyle(R.string.art_14, R.drawable.ic_style_art_14),
        ArtStyle(R.string.art_15, R.drawable.ic_style_art_15),
        ArtStyle(R.string.art_16, R.drawable.ic_style_art_16),
        ArtStyle(R.string.art_17, R.drawable.ic_style_art_17),
        ArtStyle(R.string.art_18, R.drawable.ic_style_art_18),
        ArtStyle(R.string.art_19, R.drawable.ic_style_art_19),
        ArtStyle(R.string.art_20, R.drawable.ic_style_art_20),
        ArtStyle(R.string.art_21, R.drawable.ic_style_art_21),
        ArtStyle(R.string.art_22, R.drawable.ic_style_art_22),
        ArtStyle(R.string.art_23, R.drawable.ic_style_art_23),
        ArtStyle(R.string.art_24, R.drawable.ic_style_art_24),
        ArtStyle(R.string.art_25, R.drawable.ic_style_art_25),
        ArtStyle(R.string.art_26, R.drawable.ic_style_art_26),
        ArtStyle(R.string.art_27, R.drawable.ic_style_art_27),
        ArtStyle(R.string.art_28, R.drawable.ic_style_art_28),
        ArtStyle(R.string.art_29, R.drawable.ic_style_art_29),
        ArtStyle(R.string.art_30, R.drawable.ic_style_art_30)
    )

    private val prompts = SingleLiveEvent<List<Prompt>>()
    private val artStyles = SingleLiveEvent<List<ArtStyle>>()
    val getPrompts: LiveData<List<Prompt>> by lazy {
        prompts
    }

    val getArtStyle: LiveData<List<ArtStyle>> by lazy {
        artStyles
    }

    init {
        artStyles.value = styleArtList
        prompts.value = promptList
    }

    fun selectPrompts(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<Prompt>()
            for (i in promptList.indices) {
                list.add(Prompt(promptList[i].text, promptList[i].text == id))
            }
            prompts.postValue(list)
        }
    }

    fun selectArtStyle(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<ArtStyle>()
            for (i in styleArtList.indices) {
                list.add(ArtStyle(styleArtList[i].text, styleArtList[i].image, styleArtList[i].text == id))
            }
            artStyles.postValue(list)
        }
    }
}
