package com.chatbotai.aichataiart.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.chatbotai.aichataiart.database.model.Question
import com.chatbotai.aichataiart.database.model.QuestionGroup
import com.chatbotai.aichataiart.database.model.QuestionGroupAndQuestion

@Dao
interface QuestionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestionGroup(list: List<QuestionGroup>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestion(list: List<Question>)

    @Transaction
    @Query("SELECT * FROM question_group")
    fun listQuestionGroupAndQuestion(): PagingSource<Int, QuestionGroupAndQuestion>

    @Query("SELECT (SELECT count(*) question_group) > 0")
    fun isEmpty(): Boolean

    @Query("UPDATE question SET check_select = NOT check_select WHERE question = :question")
    fun updateQuestCheck(question: Int)

    @Query("UPDATE question SET check_select = 0 WHERE check_select = 1 AND question !=:question")
    fun updateResetCheck(question: Int)

    @Query("UPDATE question SET check_select = 1 WHERE question = :ques")
    fun updateQuestion(ques: Int)

    @Query("DELETE FROM question_group")
    fun deleteGroup()

    @Query("DELETE FROM question")
    fun deleteQuestion()

}