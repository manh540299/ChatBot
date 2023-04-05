package com.chatbotai.aichataiart.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_generated",
    indices = [Index(value = arrayOf("id", "path", "style", "content_draw"))]
)
@Keep
data class ImageGenerated(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "path")
    val path: String,
    @ColumnInfo(name = "style")
    val style: String = "",
    @ColumnInfo(name = "content_draw")
    val contentDraw: String = "",
    @ColumnInfo(name = "state_check")
    val stateCheck: Int = 0
)