package com.chatbotai.aichataiart.view.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatbotai.aichataiart.databinding.ActivityTipsQuestionBinding
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.QuestionAdapter
import com.chatbotai.aichataiart.view.dialog.DialogGetMessage
import com.chatbotai.aichataiart.view.dialog.DialogPleaseInstallFromStore
import com.chatbotai.aichataiart.viewmodel.QuestionViewModel
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.smarttech.smarttechlibrary.ads.RewardedAdsManager
import com.smarttech.smarttechlibrary.utils.CommonUtils
import kotlinx.coroutines.flow.collectLatest

class TipsQuestionActivity : BaseActivityWithDataBiding<ActivityTipsQuestionBinding>(ActivityTipsQuestionBinding::inflate), OnUserEarnedRewardListener {
    private val questionViewModel: QuestionViewModel by viewModels()
    private var isFromDetailConversationActivity: Boolean = false

    private val adapterQues = QuestionAdapter({ question, isCheck ->
        questionViewModel.updateCheck(question)
        if (isCheck) {
            binding.edtQuestion.setText("")
        } else {
            binding.edtQuestion.setText(question)
        }
    }, {
        questionViewModel.refresh()
    })

    companion object {
        fun newInstance(context: Context, isFromDetailConversationActivity: Boolean): Intent {
            return Intent(context, TipsQuestionActivity::class.java).apply {
                putExtra("isFromDetailConversationActivity", isFromDetailConversationActivity)
            }
        }

        private fun receiveData(intent: Intent, result: (Boolean) -> Unit) {
            result(intent.getBooleanExtra("isFromDetailConversationActivity", false))
        }
    }

    override fun initData() {
        updateNumberRemaining()
        receiveData(intent) {
            isFromDetailConversationActivity = it
        }
    }

    override fun isIgnoreTransparent(): Boolean {
        return true
    }

    override fun initView() {
        lightStatusBar()
        binding.rlQuest.apply {
            layoutManager = LinearLayoutManager(this@TipsQuestionActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterQues
        }
    }

    override fun listenLiveData() {
        lifecycleScope.launchWhenCreated {
            questionViewModel.listQuestionGroupAndQuestion.collectLatest {
                adapterQues.submitData(it)
            }
        }
    }

    override fun listeners() {
        binding.btnRemainingMessage.setOnClickListener {
            showDialogGetMessage()
        }

        binding.edtQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.isHaveTextToSend = (count > 0 && s.toString().isNotEmpty() && s.toString().isNotBlank())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.btnSend.setOnClickListener {
            if (!ManageSaveLocal.getAccessSendMessage()) {
                if (ManageSaveLocal.getIsShowAdsRewarded()) {
                    showDialogGetMessage()
                } else {
                    showPayment(ShowPaymentFrom.QUESTION)
                }
                return@setOnClickListener
            }
            if (!CommonUtils.verifyInstallerId(this) && !ManageSaveLocal.getIsAccessSendMessageFromGoogleStore()) {
                DialogPleaseInstallFromStore(this).show()
            } else {
                showAds {
                    sendQuestion()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun sendQuestion() {
        val message = binding.edtQuestion.text.toString()
        if (isFromDetailConversationActivity) {
            Intent().apply {
                putExtra("message", message)
                setResult(DetailConversationActivity.RESULT, this)
            }
        } else {
            startActivity(DetailConversationActivity.newInstance(this, message))
            LogEventUtils.logFeature(UserFeature.CHAT)
        }
        finish()
    }

    private fun showDialogGetMessage() {
        DialogGetMessage(this) {
            if (it) {
                showPayment(ShowPaymentFrom.DETAIL_CONVERSATION)
            } else {
                RewardedAdsManager.showAds(this, this)
            }
        }.show()
    }

    override fun purchaseSuccess() {
        super.purchaseSuccess()
        sendQuestion()
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        ManageSaveLocal.setCountRewardSendMessage()
        updateNumberRemaining()
    }

    private fun updateNumberRemaining() {
        binding.numberRemaining = ManageSaveLocal.getNumberMessageRemaining()
    }

}