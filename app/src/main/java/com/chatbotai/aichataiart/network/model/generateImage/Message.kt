package com.chatbotai.aichataiart.network.model.generateImage

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Message(
    val url: String
) : Parcelable