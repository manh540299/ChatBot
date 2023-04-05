package com.chatbotai.aichataiart.view.activity

import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatbotai.aichataiart.databinding.ActivityMainBinding
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.HistoryArtMainAdapter
import com.chatbotai.aichataiart.view.dialog.RateDialog
import com.chatbotai.aichataiart.viewmodel.MainViewModel
import com.smarttech.smarttechlibrary.ads.AdsInterstitialManager
import com.smarttech.smarttechlibrary.utils.ManageSaveLocal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

class MainActivity : BaseActivityWithDataBiding<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainViewModel: MainViewModel by viewModels()
    private val imageGeneratedAdapter: HistoryArtMainAdapter by lazy {
        HistoryArtMainAdapter(action = {
            showAds {
                startActivity(PreviewHistoryActivity.newInstance(this, it))
            }
        })
    }


    override fun initData() {
        binding.mainViewModel = mainViewModel

        LogEventUtils.logFeature(UserFeature.MAIN)
    }

    override fun initView() {
        lightStatusBar()
        marginStatusBar(listOf(binding.btnSetting))
        marginNavigationBar(arrayListOf(binding.viewHistory))
        addBounceView(arrayListOf(binding.btnQuestionAI, binding.btnGenerate))

        binding.rclHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageGeneratedAdapter
        }
        checkPurchase()
    }

    override fun listenLiveData() {
        lifecycleScope.launchWhenResumed {
            mainViewModel.listImageGenerated.collectLatest {
                imageGeneratedAdapter.submitData(it)
            }
        }
    }

    override fun listeners() {
        binding.btnSetting.setOnClickListener {
            goToNewActivity(SettingActivity::class.java)
        }

        binding.btnSeeAll.setOnClickListener {
            showAds {
                goToNewActivity(HistoryActivity::class.java)
            }
        }

        binding.btnPremium.root.setOnClickListener {
            showPayment(ShowPaymentFrom.MAIN)
        }

        binding.btnQuestionAI.setOnClickListener {
            goToNewActivity(ConversationHistoryActivity::class.java)
        }

        binding.btnGenerate.setOnClickListener {
            goToNewActivity(StyleArtActivity::class.java)
        }

        onBackPressedDispatcher.addCallback {
            backAction()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            delay(200)
            if (com.chatbotai.aichataiart.utils.ManageSaveLocal.getIsShowNativeHome()){
                AdsInterstitialManager.loadNative(binding.container)
            }
            binding.rclHistory.smoothScrollToPosition(0)
        }
    }

    private fun backAction() {
        if (com.chatbotai.aichataiart.utils.ManageSaveLocal.getIsRating()) {
            finish()
        } else {
            RateDialog(this) {
                finish()
            }.show()
        }
    }

    override fun purchaseSuccess() {
        super.purchaseSuccess()
        checkPurchase()
    }

    private fun checkPurchase() {
        binding.isPurchase = ManageSaveLocal.isPurchase()
        binding.container.visibility = View.GONE
    }
}