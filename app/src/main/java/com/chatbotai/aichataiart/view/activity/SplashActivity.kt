package com.chatbotai.aichataiart.view.activity

import android.content.Intent
import android.util.Log
import androidx.activity.addCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import com.chatbotai.aichataiart.BuildConfig
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.ActivitySplashBinding
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.smarttech.smarttechlibrary.ads.RewardedAdsManager
import com.smarttech.smarttechlibrary.billing.BillingManager
import kotlinx.coroutines.delay

class SplashActivity : BaseActivityWithDataBiding<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun initData() {
        BillingManager.getInstance(this).create(this)
    }


    override fun initView() {
        marginNavigationBar(arrayListOf(binding.btnTermsOfUse))
        binding.isLoading = false

        loadImage(R.drawable.foreground, binding.imgIconApp) {
            animationIcon()
        }
    }

    override fun listenLiveData() {
    }

    override fun listeners() {
        binding.btnStart.setOnClickListener {
            LogEventUtils.logFeature(UserFeature.LET_START)
            finishAction()
        }

        binding.btnPolicy.setOnClickListener {
            CommonUtils.gotoPrivacyPolicy(this)
        }

        binding.btnTermsOfUse.setOnClickListener {
            CommonUtils.gotoTermsOfUse(this)
        }

        onBackPressedDispatcher.addCallback {

        }
    }

    private fun loadDefaultData() {
        binding.isLoading = true
        binding.isFirstOpenApp = ManageSaveLocal.getIsFirstOpenApp()
        LogEventUtils.logFeature(UserFeature.BEGIN_LOAD_CONFIG)
        FirebaseRemoteConfig.getInstance().apply {
            val builder = FirebaseRemoteConfigSettings.Builder()
            builder.minimumFetchIntervalInSeconds = 10
            builder.fetchTimeoutInSeconds = 10
            val configSettings = builder.build()
            setConfigSettingsAsync(configSettings)
            fetchAndActivate().addOnCompleteListener { itRemote ->
                if (itRemote.isSuccessful) {
                    if (!BuildConfig.DEBUG) {
                        com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setInterUnitAds(getString("it_ads_inter"))
                        com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setOpenUnitAds(getString("it_ads_start"))
                        com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setBannerUnitAds(getString("it_ads_banner"))
                        com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setNativeUnitAds(getString("it_ads_native"))
                        com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setRewardUnitAds(getString("it_ads_reward"))
                        com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setTimeLoadAds(getLong("time_load_ads"))
                        com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setTimeShowAds(getLong("time_show_ads"))
                    }
                    ManageSaveLocal.setIsAccessSendMessageFromGoogleStore(getBoolean("is_access_send_message_google_store"))
                    ManageSaveLocal.setIsShowNativeHome(getBoolean("is_show_native_home"))
                    ManageSaveLocal.setIsShowAdsRewarded(getBoolean("is_show_rewarded"))
                    ManageSaveLocal.setPlusRewardMessage(getLong("count_plus_reward_message").toInt())
                    ManageSaveLocal.setNumberLimitSendMessage(getLong("count_limit_send_message").toInt())
                    ManageSaveLocal.setCountLimitGenerateImage(getLong("count_limit_generate_image").toInt())
                }
                LogEventUtils.logFeature(UserFeature.LOAD_CONFIG_SUCCESS)
                binding.isLoading = false
                ManageSaveLocal.getIsFirstOpenApp().let {
                    binding.isFirstOpenApp = it
                    if (!it) {
                        finishAction(500)
                    }
                }
            }
        }
    }

    private fun animationIcon() {
        val transitionListener = object : MotionLayout.TransitionListener {

            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {
                // nothing to do

            }

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                // nothing to do
            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                Log.d("Splash", "onTransitionCompleted: ${ManageSaveLocal.getIsFirstOpenApp()}")
                loadDefaultData()
            }

            override fun onTransitionTrigger(p0: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
                //not used here
            }
        }
        binding.motionSplash.setTransitionListener(transitionListener)
        binding.motionSplash.transitionToEnd()
    }

    private fun finishAction(timeDelay: Long? = null) {
        lifecycleScope.launchWhenResumed {
            timeDelay?.let {
                delay(it)
            }
            if (ManageSaveLocal.getIsFirstOpenApp()) {
                Intent(this@SplashActivity, TutorialActivity::class.java).apply {
                    startActivity(this)
                    ManageSaveLocal.setIsFirstOpenApp(false)
                }
            } else {
                if (com.smarttech.smarttechlibrary.utils.ManageSaveLocal.isPurchase()) {
                    Intent(this@SplashActivity, MainActivity::class.java).apply {
                        startActivity(this)
                    }
                } else {
                    showPayment()
                }
            }
            this@SplashActivity.finish()
        }
    }
}