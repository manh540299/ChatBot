package com.chatbotai.aichataiart.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.chatbotai.aichataiart.database.dao.ContentConversationDao
import com.chatbotai.aichataiart.database.model.ContentConversation
import com.chatbotai.aichataiart.database.room.RoomDB
import com.libbtech.antivirus.security.ChCrypto

class ContentConversationRepository(application: Application) {
    private val contentConversationDao: ContentConversationDao by lazy {
        RoomDB.getInstance(application).contentConversation()
    }

    companion object {
        private var instance: ContentConversationRepository? = null

        fun newInstance(application: Application): ContentConversationRepository {
            if (instance == null) {
                instance = ContentConversationRepository(application)
            }
            return instance!!
        }
    }

    fun loadAll(id: Long, textSearch: String): PagingSource<Int, ContentConversation> {
        return contentConversationDao.loadAll(id, "%$textSearch%")
    }

    fun isEmpty(): LiveData<Boolean> {
        return contentConversationDao.isEmpty()
    }

    fun isTyping(id: Long): LiveData<Boolean> {
        return contentConversationDao.isTyping(id)
    }

    fun getMessageBotNotAnswer(id: Long): String? {
        return contentConversationDao.getMessageBotNotAnswer(id)
    }

    fun checkHaveMessageBotNotAnswer(id: Long): Boolean {
        return contentConversationDao.checkHaveMessageBotNotAnswer(id)
    }

    fun insertConversation(contentConversation: ContentConversation) {
        contentConversationDao.insert(contentConversation)
    }

    fun updateMessageBot(message: String, id: Long, resultMessage: (String) -> Unit) {
        val messageDecode = try {
            ChCrypto.aesDecrypt(message)
        } catch (ex: NullPointerException) {
            ex.message ?: ""
        }
        contentConversationDao.updateMessageBot(messageDecode, id)
        resultMessage(messageDecode)
    }

    fun getMessageOld(id: Long): List<String> {
        return contentConversationDao.getMessageOld(id)
    }

    fun delete(id: Long? = null) {
        if (id != null) {
            contentConversationDao.delete(id)
        } else {
            contentConversationDao.delete()
        }
    }

    fun deleteMessageBotNotAnswer() {
        contentConversationDao.deleteMessageBotNotAnswer()
    }

}