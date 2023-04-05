package com.chatbotai.aichataiart.utils

import androidx.paging.PagingConfig
import com.libbtech.antivirus.security.ChCrypto
import okhttp3.Request

object Constants {
    var TIMEOUT = 120L
    val PAGE_CONFIG = PagingConfig(pageSize = 12)
    var GPT_TYPING = "AI ChatBot is typing"
    var GPT_TYPING_DEFAULT = "AI ChatBot is typing"
    var GPT_TYPING_ERROR = "Server is busy. Please try again later"

    fun createBuilderDemo(builder: Request.Builder): Request.Builder {
        return builder.apply {
            val code = try {
                ChCrypto.aesEncrypt("${System.currentTimeMillis() / 1000}")
            } catch (_: UnsatisfiedLinkError) {
                ""
            }catch (_ : NoClassDefFoundError){
                ""
            }
//            Log.d("createBuilderDemo", "$code ::: ${ChCrypto.aesDecrypt(code)}")
            addHeader("btoken", code)
        }
    }
}