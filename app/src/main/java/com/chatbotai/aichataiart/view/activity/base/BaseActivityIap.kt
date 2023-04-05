package com.chatbotai.aichataiart.view.activity.base

import android.os.CountDownTimer
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

abstract class BaseActivityIap<Binding : ViewDataBinding>(inflate: Inflate<Binding>) : BaseActivityWithDataBiding<Binding>(inflate) {
    private val configDampingRatio = arrayListOf(true, true, true, true, false)
    private var indexCurrent: Int = 0
    private lateinit var countDownTimer: CountDownTimer

    protected fun startAnimation(
        startScale: Float,
        toScale: Float,
        view: View,
        viewAlphaHover: View,
        viewAlphaHover1: View,
        viewAlphaHover2: View,
        viewAlphaHover3: View
    ) {
        if (indexCurrent == configDampingRatio.size) {
            indexCurrent = 0
        }
        val result = configDampingRatio[indexCurrent]
        if (!result) {
            cancelCountDownTimer()
            countDownTimer = object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    indexCurrent++
                    startAnimation(startScale, toScale, view, viewAlphaHover, viewAlphaHover1, viewAlphaHover2, viewAlphaHover3)
                }
            }.start()
            return
        }
        view.scaleX = startScale
        view.scaleY = startScale
        indexCurrent++
        SpringAnimation(view, DynamicAnimation.SCALE_X, toScale).apply {
            spring.dampingRatio = 1f
            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
            addEndListener { animation, canceled, value, velocity ->
                startAnimation(toScale, startScale, view, viewAlphaHover, viewAlphaHover1, viewAlphaHover2, viewAlphaHover3)
            }
            start()
        }
        SpringAnimation(view, DynamicAnimation.SCALE_Y, toScale).apply {
            spring.dampingRatio = 1f
            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
            start()
        }
        if (startScale > toScale) {
            startAnimationAlpha(viewAlphaHover, 1.34f, 0.2f)
            startAnimationAlpha(viewAlphaHover1, 1.26f, 0.17f)
            startAnimationAlpha(viewAlphaHover2, 1.2f, 0.12f)
            startAnimationAlpha(viewAlphaHover3, 1.16f, 0.08f)
        }
    }

    private fun startAnimationAlpha(view: View, finalScale: Float, alpha: Float) {
        view.alpha = alpha
        view.scaleX = 0.95f
        view.scaleY = 0.95f
        SpringAnimation(view, DynamicAnimation.ALPHA, 0f).apply {
            spring.dampingRatio = 4f
            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
            start()
        }
        SpringAnimation(view, DynamicAnimation.SCALE_X, finalScale).apply {
            spring.dampingRatio = 4f
            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
            start()
        }
        SpringAnimation(view, DynamicAnimation.SCALE_Y, finalScale).apply {
            spring.dampingRatio = 4f
            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
            start()
        }
    }

    private fun cancelCountDownTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelCountDownTimer()
    }
}