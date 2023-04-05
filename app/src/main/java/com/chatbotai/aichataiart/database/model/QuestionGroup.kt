package com.chatbotai.aichataiart.database.model

import androidx.annotation.Keep
import androidx.room.*


@Entity(
    tableName = "question_group",
    indices = [Index(
        value = arrayOf("name_group"),
        unique = true
    )]
)
@Keep
data class QuestionGroup(
    @PrimaryKey
    @ColumnInfo(name = "res_icon")
    val resIcon: Int,
    @ColumnInfo(name = "name_group")
    val nameGroup: Int
)

@Entity(
    tableName = "question",
    indices = [Index(value = arrayOf("question"), unique = true)]
)
@Keep
data class Question(
    @PrimaryKey
    @ColumnInfo(name = "question")
    val question: Int,
    @ColumnInfo(name = "foreign_key")
    val foreignKey: Int,
    @ColumnInfo(name = "check_select")
    val check: Boolean
)

@Keep
data class QuestionGroupAndQuestion(
    @Embedded val questionGroup: QuestionGroup,
    @Relation(
        parentColumn = "name_group",
        entityColumn = "foreign_key"
    )
    val listQuestion: List<Question>
)