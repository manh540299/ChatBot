package com.chatbotai.aichataiart.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chatbotai.aichataiart.database.model.Conversation
import java.util.*

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation WHERE name_conversation LIKE :textSearch order by time desc")
    fun loadAll(textSearch: String): PagingSource<Int, Conversation>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(conversation: Conversation): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list: List<Conversation>)

    @Query("UPDATE conversation set time=:time WHERE id =:id")
    fun updateTime(id: Long, time: Long = Calendar.getInstance().time.time)

    @Query("SELECT name_conversation from conversation WHERE id =:id")
    fun messageMain(id: Long): String

    @Query("SELECT (SELECT count(*) FROM conversation) = 0")
    fun isEmpty(): LiveData<Boolean>

    @Query("delete from conversation where id = :id")
    fun delete(id: Long)

    @Query("delete from conversation")
    fun delete()
}