package com.chatbotai.aichataiart.bus

import androidx.annotation.Keep

@Keep
data class NetworkState(val status: State, val message: String? = null) {
    enum class State {
        FAILED, LOADED, LOADING
    }
}