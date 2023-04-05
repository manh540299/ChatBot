package com.chatbotai.aichataiart.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "content_conversation",
    indices = [Index(value = arrayOf("id", "is_bot", "id_conversation", "time"))]
)
@Keep
data class ContentConversation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "is_bot")
    val isBot: Boolean,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "id_conversation")
    val idConversation: Long
)