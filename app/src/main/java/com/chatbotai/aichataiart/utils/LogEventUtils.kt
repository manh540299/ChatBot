package com.chatbotai.aichataiart.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import com.chatbotai.aichataiart.view.activity.ShowPaymentFrom
import com.google.firebase.analytics.FirebaseAnalytics

class LogEventUtils {
    companion object {
        private lateinit var firebaseAnalytics: FirebaseAnalytics
        fun createInstance(context: Context) {
            if (::firebaseAnalytics.isInitialized) {
                return
            }
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        }

        fun logIAP(showPaymentFrom: ShowPaymentFrom, idProduct: String) {
            if (::firebaseAnalytics.isInitialized) {
                val bundle = Bundle()
                bundle.putString("ID_PRODUCT", idProduct)
                bundle.putString("IAP_FROM", showPaymentFrom.name.lowercase())
                Log.d("LogEventUtils", "logIAP=${showPaymentFrom.name.lowercase()}===>$bundle")
                firebaseAnalytics.logEvent("user_iap_subscribe", bundle)
            }
        }

        fun logFeature(userFeature: UserFeature) {
            if (::firebaseAnalytics.isInitialized) {
                val bundle = Bundle()
                bundle.putString("FEATURE", userFeature.name.lowercase())
                Log.d("LogEventUtils", "${userFeature.name.lowercase()}===>$bundle")
                firebaseAnalytics.logEvent("user_feature", bundle)
            }
        }

        fun logFeedBack(content: String) {
            if (::firebaseAnalytics.isInitialized) {
                val bundle = Bundle()
                bundle.putString("FEEDBACK_CONTENT", content)
                Log.d("LogEventUtils", "logFeedBack=$bundle")
                firebaseAnalytics.logEvent("user_feedback", bundle)
            }
        }
    }
}

@Keep
enum class UserFeature {
    BEGIN_LOAD_CONFIG, LOAD_CONFIG_SUCCESS, LET_START, TUTORIAL_ONE, TUTORIAL_TWO, TUTORIAL_FINISH, IAP, MAIN, QUESTION_AI, NEW_CHAT, CHAT, CHOOSE_STYLE_ART, CREATE_ART, CREATE_ART_SUCCESS
}
