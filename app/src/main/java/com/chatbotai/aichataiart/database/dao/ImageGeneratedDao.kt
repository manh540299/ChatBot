package com.chatbotai.aichataiart.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chatbotai.aichataiart.database.model.ImageGenerated
import java.util.*

@Dao
interface ImageGeneratedDao {
    @Query("SELECT * FROM image_generated order by id desc")
    fun loadAll(): PagingSource<Int, ImageGenerated>

    @Query("SELECT * FROM image_generated order by id desc limit 10")
    fun loadPre(): PagingSource<Int, ImageGenerated>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageGenerated: ImageGenerated): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<ImageGenerated>)

    @Query("SELECT count(*) FROM image_generated WHERE state_check == 2")
    fun numberItemSelected(): LiveData<Int>

    @Query("SELECT * FROM image_generated WHERE id=:id")
    fun getImageGenerated(id: Long): LiveData<ImageGenerated>

    @Query("SELECT (SELECT count(*) FROM image_generated WHERE state_check == 2) = (SELECT count(*) FROM image_generated)")
    fun isSelectAll(): LiveData<Boolean>

    @Query("SELECT (SELECT count(*) FROM image_generated) = 0")
    fun isEmpty(): LiveData<Boolean>


    @Query("SELECT path FROM image_generated WHERE id =:id")
    fun getPath(id: Long): String

    @Query("SELECT state_check FROM image_generated WHERE id =:id")
    fun getStateCheck(id: Long): Int

    @Query("update image_generated set state_check =:stateCheck")
    fun updateStateCheck(stateCheck: Int)

    @Query("update image_generated set state_check =:stateCheck WHERE id =:id")
    fun updateStateCheck(stateCheck: Int, id: Long)

    @Query("delete from image_generated WHERE state_check = 2")
    fun deleteItemSelected()

    @Query("delete from image_generated where id = :id")
    fun delete(id: Long)

    @Query("delete from image_generated")
    fun delete()
}