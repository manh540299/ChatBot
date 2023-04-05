package com.chatbotai.aichataiart.utils

import android.annotation.SuppressLint
import android.content.Context
import com.chatbotai.aichataiart.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object TimeUtils {
    fun getTimeFormatApp(time: Long): String {
        if (isSameDay(time)) {
            val date = Date(time)
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
        }
        val date = Date(time)
        return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(date)
    }

    fun getTimeHistoryConversation(time: Long, context: Context): String {
        return if (isSameDay(time)) {
            context.getString(R.string.today)
        } else if (isYesterday(time)) {
            context.getString(R.string.yesterday)
        } else {
            val date = Date(time)
            DateFormat.getDateInstance(DateFormat.SHORT).format(date)
        }
    }

    fun isSameDay(time: Long): Boolean {
        return getTimeFollow() == getTimeFollow(time)
    }

    fun isYesterday(timeNew: Long, timeOld: Long = Calendar.getInstance().time.time): Boolean {
        return abs(getTimeFollow(timeNew) - getTimeFollow(timeOld)) >= 1
    }


    @SuppressLint("SimpleDateFormat")
    fun getTimeFollow(): Int {
        val date = Date(Calendar.getInstance().time.time)
        val format = SimpleDateFormat("yyyyMMdd")
        return format.format(date).toInt()
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeFollow(time: Long): Int {
        val date = Date(time)
        val format = SimpleDateFormat("yyyyMMdd")
        return format.format(date).toInt()
    }

}