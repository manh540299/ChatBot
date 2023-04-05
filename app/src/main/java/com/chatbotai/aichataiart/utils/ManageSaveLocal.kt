package com.chatbotai.aichataiart.utils

import android.util.Log
import com.chatbotai.aichataiart.R
import com.orhanobut.hawk.Hawk

object ManageSaveLocal {
    fun setIsFirstOpenApp(isFirstOpenAds: Boolean) {
        Hawk.put("is_first_open_app", isFirstOpenAds)
    }

    fun getIsFirstOpenApp(): Boolean {
        return Hawk.get("is_first_open_app", true)
    }

    fun setIsOnSpeaker(isSpeaker: Boolean) {
        Hawk.put("isSpeaker", isSpeaker)
    }

    fun getIsOnSpeaker(): Boolean {
        return Hawk.get("isSpeaker", true)
    }

    fun setIsSmartOrAdvance(isSpeaker: Boolean) {
        Hawk.put("is_smart_or_advance", isSpeaker)
    }

    fun isSmartOrAdvance(): Boolean {
        return Hawk.get("is_smart_or_advance", true)
    }

    fun getAccessSendMessage(): Boolean {
        return (Hawk.get("count_send_message", 0) < getNumberLimitSendMessage()) || com.smarttech.smarttechlibrary.utils.ManageSaveLocal.isPurchase()
    }

    fun setCountAccessSendMessage() {
        Hawk.put("count_send_message", Hawk.get("count_send_message", 0) + 1)
    }

    fun setCountRewardSendMessage() {
        Hawk.put("count_send_message", Hawk.get("count_send_message", 0) - getPlusRewardMessage())
    }

    fun getNumberMessageRemaining(): Int {
        return if (com.smarttech.smarttechlibrary.utils.ManageSaveLocal.isPurchase() || !getIsShowAdsRewarded()) -1 else getNumberLimitSendMessage() - Hawk.get("count_send_message", 0)
    }

    fun setResetAccessSendMessage() {
        Hawk.put("count_send_message", 0)
//        Hawk.put("count_generate_image", 0)
    }

    fun setPlusRewardMessage(count: Int) {
        Hawk.put("count_plus_reward_message", count)
    }

    private fun getPlusRewardMessage(): Int {
        return Hawk.get("count_plus_reward_message", 1)
    }

    fun getAccessGenerateImage(): Boolean {
        return (Hawk.get("count_generate_image", 0) < getCountLimitGenerateImage()) || com.smarttech.smarttechlibrary.utils.ManageSaveLocal.isPurchase()
    }

    fun setCountAccessGenerateImage() {
        Hawk.put("count_generate_image", Hawk.get("count_generate_image", 0) + 1)
    }


    fun setCountLimitGenerateImage(count: Int) {
        Hawk.put("count_limit_generate_image", count)
    }

    private fun getCountLimitGenerateImage(): Int {
        return Hawk.get("count_limit_generate_image", 0)
    }

    fun setLanguage(language: Int) {
        Hawk.put("language", language)
    }

    fun getLanguage(): Int {
        return Hawk.get("language", R.string.english)
    }

    fun setNumberLimitSendMessage(number: Int) {
        Log.d("setNumberLimitSend", "$number")
        if (number != 0) {
            Hawk.put("count_limit_send_message", number)
        }
    }

    private fun getNumberLimitSendMessage(): Int {
        return Hawk.get("count_limit_send_message", 3)
    }

    fun setIsRating(isLock: Boolean) {
        Hawk.put("is_rating", isLock)
    }

    fun getIsRating(): Boolean {
        return Hawk.get("is_rating", false)
    }

    // Remote Config

    fun setIsAccessSendMessageFromGoogleStore(isShow: Boolean) {
        Hawk.put("is_access_send_message_google_store", isShow)
    }

    fun getIsAccessSendMessageFromGoogleStore(): Boolean {
        return Hawk.get("is_access_send_message_google_store", false)
    }

    fun setIsShowNativeHome(isShow: Boolean) {
        Hawk.put("is_show_native_home", isShow)
    }

    fun getIsShowNativeHome(): Boolean {
        return Hawk.get("is_show_native_home", true)
    }

    fun setIsShowAdsRewarded(isShow: Boolean) {
        Hawk.put("is_show_rewarded", isShow)
    }

    fun getIsShowAdsRewarded(): Boolean {
        return Hawk.get("is_show_rewarded", true)
    }
}