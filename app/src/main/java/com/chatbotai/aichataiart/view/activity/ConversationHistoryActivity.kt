package com.chatbotai.aichataiart.view.activity

import android.text.Editable
import android.text.TextWatcher
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatbotai.aichataiart.database.model.Conversation
import com.chatbotai.aichataiart.databinding.ActivityConversationHistoryBinding
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.ConversationAdapter
import com.chatbotai.aichataiart.view.dialog.DialogQuestionClearConversation
import com.chatbotai.aichataiart.view.dialog.TypeCleanConversation
import com.chatbotai.aichataiart.viewmodel.ConversationHistoryViewModel
import com.smarttech.smarttechlibrary.utils.CommonUtils
import kotlinx.coroutines.flow.collectLatest

class ConversationHistoryActivity : BaseActivityWithDataBiding<ActivityConversationHistoryBinding>(ActivityConversationHistoryBinding::inflate) {
    override fun isIgnoreTransparent(): Boolean {
        return true
    }

    private val conversationHistoryViewModel: ConversationHistoryViewModel by viewModels()
    private val conversationAdapter: ConversationAdapter by lazy {
        ConversationAdapter({
            when (it) {
                is Conversation -> {
                    DialogQuestionClearConversation(this, TypeCleanConversation.ONLY_CONVERSATION) {
                        conversationHistoryViewModel.deleteConversation(it.id)
                    }.show()
                }
                is Long -> {
                    showAds {
                        startActivity(DetailConversationActivity.newInstance(this, it))
                    }
                }
            }
        }, { isShowAds ->
            (binding.imgNoConversation.layoutParams as? ConstraintLayout.LayoutParams)?.let {
                it.verticalBias = if (!isShowAds) 0.4f else 0.85f
                binding.imgNoConversation.layoutParams = it
            }
        })
    }

    override fun initData() {
        binding.conversationHistoryViewModel = conversationHistoryViewModel
    }

    override fun initView() {
        lightStatusBar()

        binding.rclConversation.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ConversationHistoryActivity, LinearLayoutManager.VERTICAL, false)
            adapter = conversationAdapter
        }

        LogEventUtils.logFeature(UserFeature.QUESTION_AI)
    }

    override fun listenLiveData() {
        lifecycleScope.launchWhenCreated {
            conversationHistoryViewModel.listConversations.collectLatest {
                conversationAdapter.submitData(it)
            }
        }
    }

    override fun listeners() {
        binding.edtSearch.setOnFocusChangeListener { v, hasFocus ->
            binding.isSearching = hasFocus
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.isHaveText = count > 0
                conversationHistoryViewModel.search(s.toString())
                conversationAdapter.refresh()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnCleanAll.setOnClickListener {
            DialogQuestionClearConversation(this) {
                conversationHistoryViewModel.deleteConversation()
            }.show()
        }

        binding.btnNewChat.setOnClickListener {
            showAds {
                goToNewActivity(TipsQuestionActivity::class.java)
                LogEventUtils.logFeature(UserFeature.NEW_CHAT)
            }
        }

        binding.btnClearTextSearch.setOnClickListener {
            binding.edtSearch.setText("")
        }

        binding.btnCancel.setOnClickListener {
            backAction()
        }

        binding.btnBack.setOnClickListener {
            backAction()
        }

        onBackPressedDispatcher.addCallback {
            backAction()
        }
    }

    private fun backAction() {
        if (binding.isSearching == true) {
            binding.isSearching = false
            binding.edtSearch.setText("")
            binding.edtSearch.clearFocus()
            CommonUtils.hideKeyBroad(this, binding.edtSearch)
        } else {
            finish()
        }
    }

}