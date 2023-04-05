package com.chatbotai.aichataiart.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chatbotai.aichataiart.database.model.ContentConversation

@Dao
interface ContentConversationDao {
    @Query("SELECT * FROM content_conversation WHERE id_conversation =:id AND message LIKE :textSearch order by time desc")
    fun loadAll(id: Long, textSearch: String): PagingSource<Int, ContentConversation>

    @Query("SELECT (SELECT count(*) FROM content_conversation) = 0")
    fun isEmpty(): LiveData<Boolean>

    @Query("SELECT (SELECT count(*) FROM content_conversation WHERE is_bot = 1 AND id_conversation =:id AND message = '') > 0")
    fun isTyping(id: Long): LiveData<Boolean>

    @Query("SELECT message FROM content_conversation WHERE is_bot = 0 AND id_conversation =:id order by time asc limit 1")
    fun getMessageBotNotAnswer(id: Long): String?

    @Query("SELECT (SELECT count(*) FROM content_conversation WHERE is_bot = 1 AND id_conversation =:id AND message = '') > 0")
    fun checkHaveMessageBotNotAnswer(id: Long): Boolean

    @Query("SELECT message FROM content_conversation WHERE is_bot = 0 AND id_conversation =:id order by time desc")
    fun getMessageOld(id: Long): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(conversation: ContentConversation)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list: List<ContentConversation>)


    @Query("UPDATE content_conversation set message =:message WHERE is_bot = 1 AND id_conversation =:id AND message = ''")
    fun updateMessageBot(message: String, id: Long)

    @Query("DELETE FROM content_conversation WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM content_conversation WHERE id_conversation = :idConversation")
    fun deleteFromConversation(idConversation: Long)

    @Query("DELETE FROM content_conversation")
    fun delete()

    @Query("DELETE FROM content_conversation WHERE is_bot = 1")
    fun deleteMessageBotNotAnswer()
}