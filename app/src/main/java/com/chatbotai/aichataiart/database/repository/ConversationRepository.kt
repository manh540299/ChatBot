package com.chatbotai.aichataiart.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.chatbotai.aichataiart.database.dao.ContentConversationDao
import com.chatbotai.aichataiart.database.dao.ConversationDao
import com.chatbotai.aichataiart.database.model.Conversation
import com.chatbotai.aichataiart.database.room.RoomDB

class ConversationRepository(application: Application) {
    private val conversationDao: ConversationDao by lazy {
        RoomDB.getInstance(application).conversationDao()
    }

    private val contentConversationDao: ContentConversationDao by lazy {
        RoomDB.getInstance(application).contentConversation()
    }

    companion object {
        private var instance: ConversationRepository? = null

        fun newInstance(application: Application): ConversationRepository {
            if (instance == null) {
                instance = ConversationRepository(application)
            }
            return instance!!
        }
    }

    fun loadAll(textSearch: String): PagingSource<Int, Conversation> {
        return conversationDao.loadAll("%$textSearch%")
    }

    fun messageMain(id: Long): String {
        return conversationDao.messageMain(id)
    }

    fun isEmpty(): LiveData<Boolean> {
        return conversationDao.isEmpty()
    }

    fun insertConversation(conversation: Conversation): Long {
        return conversationDao.insert(conversation)
    }

    fun updateTime(id: Long) {
        conversationDao.updateTime(id)
    }

    fun delete(id: Long? = null) {
        if (id != null) {
            conversationDao.delete(id)
            contentConversationDao.deleteFromConversation(id)
        } else {
            conversationDao.delete()
            contentConversationDao.delete()
        }
    }

}