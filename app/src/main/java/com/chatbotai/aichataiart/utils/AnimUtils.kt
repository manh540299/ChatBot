package com.chatbotai.aichataiart.utils

import android.view.View
import com.github.hariprasanths.bounceview.BounceView

object AnimUtils {
    fun boundView(view: View) {
        BounceView.addAnimTo(view).setScaleForPopOutAnim(1.02f, 1.02f).setScaleForPushInAnim(1.02f, 1.02f)
    }
}