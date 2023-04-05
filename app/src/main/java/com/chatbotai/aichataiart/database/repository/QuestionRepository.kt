package com.chatbotai.aichataiart.database.repository

import android.app.Application
import androidx.paging.PagingSource
import com.chatbotai.aichataiart.database.dao.QuestionDAO
import com.chatbotai.aichataiart.database.model.Question
import com.chatbotai.aichataiart.database.model.QuestionGroup
import com.chatbotai.aichataiart.database.model.QuestionGroupAndQuestion
import com.chatbotai.aichataiart.database.room.RoomDB

class QuestionRepository(application: Application) {
    private val questionDAO: QuestionDAO by lazy {
        RoomDB.getInstance(application).questionDAO()
    }

    companion object {
        private var instance: QuestionRepository? = null
        fun newInstant(application: Application): QuestionRepository {
            if (instance == null)
                instance = QuestionRepository(application)
            return instance!!
        }
    }

    fun listQuestionGroupAndQuestion(): PagingSource<Int, QuestionGroupAndQuestion> {
        return questionDAO.listQuestionGroupAndQuestion()
    }

    fun insertQuestion(listQuestion: List<Question>) {
        questionDAO.deleteQuestion()
        questionDAO.insertQuestion(listQuestion)
    }

    fun isEmpty(): Boolean{
        return questionDAO.isEmpty()
    }

    fun insertQuestionGroup(listQuestionGroup: List<QuestionGroup>) {
        questionDAO.deleteGroup()
        questionDAO.insertQuestionGroup(listQuestionGroup)
    }

    fun updateCheck(question: Int) {
        questionDAO.updateResetCheck(question)
        questionDAO.updateQuestCheck(question)
    }

    fun delete() {
        questionDAO.deleteGroup()
        questionDAO.deleteQuestion()
    }
}