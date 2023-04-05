package com.chatbotai.aichataiart.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.chatbotai.aichataiart.database.dao.ImageGeneratedDao
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.database.room.RoomDB

class ImageGeneratedRepository(application: Application) {
    private val imageGeneratedDao: ImageGeneratedDao by lazy {
        RoomDB.getInstance(application).imageGeneratedDao()
    }

    companion object {
        private var instance: ImageGeneratedRepository? = null

        fun newInstance(application: Application): ImageGeneratedRepository {
            if (instance == null) {
                instance = ImageGeneratedRepository(application)
            }
            return instance!!
        }
    }

    fun loadAll(): PagingSource<Int, ImageGenerated> {
        return imageGeneratedDao.loadAll()
    }

    fun loadPre(): PagingSource<Int, ImageGenerated> {
        return imageGeneratedDao.loadPre()
    }

    fun isEmpty(): LiveData<Boolean> {
        return imageGeneratedDao.isEmpty()
    }

    fun isSelectAll(): LiveData<Boolean> {
        return imageGeneratedDao.isSelectAll()
    }

    fun getImageGenerated(id: Long): LiveData<ImageGenerated> {
        return imageGeneratedDao.getImageGenerated(id)
    }

    fun numberItemSelected(): LiveData<Int> {
        return imageGeneratedDao.numberItemSelected()
    }

    fun insert(imageGenerated: ImageGenerated): Long {
        return imageGeneratedDao.insert(imageGenerated)
    }

    fun itemClick(id: Long) {
        val stateCheck = imageGeneratedDao.getStateCheck(id)
        if (stateCheck == 1) {
            imageGeneratedDao.updateStateCheck(2, id)
        } else if (stateCheck == 2) {
            imageGeneratedDao.updateStateCheck(1, id)
        }
    }

    fun deleteItemSelected() {
        imageGeneratedDao.deleteItemSelected()
    }

    fun updateDeselect() {
        imageGeneratedDao.updateStateCheck(0)
    }

    fun updateBeginSelect(id: Long? = null) {
        imageGeneratedDao.updateStateCheck(1)
        id?.let {
            imageGeneratedDao.updateStateCheck(2, it)
        }
    }

    fun updateSelect() {
        imageGeneratedDao.updateStateCheck(2)
    }

    fun getPath(id: Long): String {
        return imageGeneratedDao.getPath(id)
    }

    fun delete(id: Long?) {
        if (id != null) {
            imageGeneratedDao.delete(id)
        } else {
            imageGeneratedDao.delete()
        }
    }

}