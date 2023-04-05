package com.chatbotai.aichataiart.database.room

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chatbotai.aichataiart.database.dao.ContentConversationDao
import com.chatbotai.aichataiart.database.dao.ConversationDao
import com.chatbotai.aichataiart.database.dao.ImageGeneratedDao
import com.chatbotai.aichataiart.database.dao.QuestionDAO
import com.chatbotai.aichataiart.database.model.*

@Database(
    entities = [Conversation::class, ContentConversation::class, Question::class, QuestionGroup::class, ImageGenerated::class],
    version = 6,
    exportSchema = false
)
abstract class RoomDB : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun contentConversation(): ContentConversationDao
    abstract fun questionDAO(): QuestionDAO
    abstract fun imageGeneratedDao(): ImageGeneratedDao


    companion object {
        private const val DATABASE_NAME = "ai_chat_bot"
        private var sInstance: RoomDB? = null

        fun getInstance(application: Application): RoomDB {
            synchronized(RoomDB::class.java) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                        application,
                        RoomDB::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return sInstance!!
        }

        fun getInstance(context: Context): RoomDB {
            synchronized(RoomDB::class.java) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                        context,
                        RoomDB::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return sInstance!!
        }
    }
}