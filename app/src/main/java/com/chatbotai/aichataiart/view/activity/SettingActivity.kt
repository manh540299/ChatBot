package com.chatbotai.aichataiart.view.activity

import android.widget.Toast
import androidx.activity.addCallback
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.ActivitySettingBinding
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.dialog.DialogFeedBack
import com.chatbotai.aichataiart.view.dialog.DialogSelectMode
import com.chatbotai.aichataiart.view.dialog.RateDialog
import com.smarttech.smarttechlibrary.billing.BillingManager
import com.smarttech.smarttechlibrary.utils.ManageSaveLocal

class SettingActivity : BaseActivityWithDataBiding<ActivitySettingBinding>(ActivitySettingBinding::inflate) {
    override fun initData() {

    }

    override fun initView() {
        lightStatusBar()
        marginStatusBar(listOf(binding.btnBack))
        marginNavigationBar(listOf(binding.btnVersion))
        checkPurchase()
        binding.swVoice.isChecked = com.chatbotai.aichataiart.utils.ManageSaveLocal.getIsOnSpeaker()
    }

    override fun listenLiveData() {

    }

    override fun listeners() {
        binding.btnPremium.root.setOnClickListener {
            showPayment(ShowPaymentFrom.SETTING)
        }

        binding.btnBack.setOnClickListener {
            backAction()
        }

        onBackPressedDispatcher.addCallback {
            backAction()
        }

        binding.btnLanguage.setOnClickListener {
            goToNewActivity(LanguageActivity::class.java)
        }

        binding.btnVoice.setOnClickListener {
            binding.swVoice.isChecked = !binding.swVoice.isChecked
            com.chatbotai.aichataiart.utils.ManageSaveLocal.setIsOnSpeaker(binding.swVoice.isChecked)
        }

        binding.btnSelectMode.setOnClickListener {
            DialogSelectMode(this).show()
        }

        binding.btnHelp.setOnClickListener {
            DialogFeedBack(this).show()
        }

        binding.btnRestore.setOnClickListener {
            BillingManager.getInstance(this).create(this)
            Toast.makeText(this, getString(R.string.restore_purchases_success), Toast.LENGTH_SHORT).show()
        }

        binding.btnRate.setOnClickListener {
            RateDialog(this).show()
        }

        binding.btnShare.setOnClickListener {
            com.smarttech.smarttechlibrary.utils.CommonUtils.shareApp(this)
        }

        binding.btnTermsOfUse.setOnClickListener {
            CommonUtils.gotoTermsOfUse(this)
        }

        binding.btnPolicy.setOnClickListener {
            CommonUtils.gotoPrivacyPolicy(this)
        }

        binding.btnVersion.setOnClickListener {
            com.smarttech.smarttechlibrary.utils.CommonUtils.goToChPlay(this)
        }
    }

    private fun backAction() {
        finish()
    }

    override fun purchaseSuccess() {
        super.purchaseSuccess()
        checkPurchase()
    }

    private fun checkPurchase() {
        binding.isPurchase = ManageSaveLocal.isPurchase()
    }
}