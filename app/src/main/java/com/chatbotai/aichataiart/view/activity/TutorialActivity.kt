package com.chatbotai.aichataiart.view.activity

import android.view.View
import androidx.activity.addCallback
import androidx.annotation.Keep
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.ActivityTutorialBinding
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithoutDataBiding
import com.chatbotai.aichataiart.view.adapter.TutorialPagerAdapter
import com.smarttech.smarttechlibrary.utils.ManageSaveLocal

class TutorialActivity : BaseActivityWithoutDataBiding<ActivityTutorialBinding>(ActivityTutorialBinding::inflate) {
    override fun initData() {
        lightStatusBar()
        LogEventUtils.logFeature(UserFeature.TUTORIAL_ONE)
    }

    override fun initView() {
        marginNavigationBar(listOf(binding.btnContinue))
        val listTutorial = ArrayList<Tutorial>()
        listTutorial.apply {
            add(Tutorial(R.drawable.img_tutorial_one, R.string.get_an_answer_to_any_questions))
            add(Tutorial(R.drawable.img_tutorial_two, R.string.turn_words_into_art))
        }

        binding.viewPagerTutorial.apply {
            adapter = TutorialPagerAdapter(this@TutorialActivity, listTutorial)
            setPageTransformer(FadePageTransformer())
        }
    }


    override fun listenLiveData() {

    }

    override fun listeners() {
        binding.btnContinue.setOnClickListener {
            if (binding.viewPagerTutorial.currentItem == 1) {
                backAction()
            } else {
                binding.viewPagerTutorial.currentItem = 1
            }
        }

        onBackPressedDispatcher.addCallback {
            backAction()
        }

        binding.viewPagerTutorial.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 1) {
                    LogEventUtils.logFeature(UserFeature.TUTORIAL_TWO)
                }
            }
        })
    }

    private fun backAction() {
        if (ManageSaveLocal.isPurchase()) {
            goToNewActivity(MainActivity::class.java)
        } else {
            showPayment()
        }
        LogEventUtils.logFeature(UserFeature.TUTORIAL_FINISH)
        finish()
    }

    class FadePageTransformer : ViewPager2.PageTransformer {

        override fun transformPage(page: View, position: Float) {
            val pageWidth = page.width

            if (position < -1) {
                page.alpha = 0f
            } else if (position <= 0) {
                page.alpha = 1f
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            } else if (position <= 1) {
                page.alpha = 1 - position
                page.translationX = pageWidth * -position
            } else {
                page.alpha = 0f
            }
        }
    }

    @Keep
    data class Tutorial(val resResource: Int, val title: Int) : java.io.Serializable
}