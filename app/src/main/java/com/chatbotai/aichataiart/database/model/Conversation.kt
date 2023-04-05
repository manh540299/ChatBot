package com.chatbotai.aichataiart.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversation",
    indices = [Index(value = arrayOf("id", "name_conversation"))]
)
@Keep
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name_conversation")
    val nameConversation: String,
    @ColumnInfo(name = "time")
    val time: Long
)