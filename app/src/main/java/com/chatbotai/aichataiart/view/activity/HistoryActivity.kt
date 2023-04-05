package com.chatbotai.aichataiart.view.activity

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.databinding.ActivityHistoryBinding
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.HistoryArtAdapter
import com.chatbotai.aichataiart.view.dialog.DialogQuestionClearConversation
import com.chatbotai.aichataiart.view.dialog.TypeCleanConversation
import com.chatbotai.aichataiart.viewmodel.HistoryViewModel
import com.smarttech.smarttechlibrary.utils.CommonUtils
import kotlinx.coroutines.flow.collectLatest

class HistoryActivity : BaseActivityWithDataBiding<ActivityHistoryBinding>(ActivityHistoryBinding::inflate) {
    private val historyViewModel: HistoryViewModel by viewModels()
    private val imageGeneratedAdapter: HistoryArtAdapter by lazy {
        HistoryArtAdapter {
            historyViewModel.itemClick(it) {
                showAds {
                    openHistory(it)
                }
            }
        }
    }

    override fun initData() {
        lightStatusBar()
        marginStatusBar(listOf(binding.btnBack))
        marginNavigationBar(listOf(binding.btnShare))
        paddingNavigationBar(listOf(binding.rclHistory))
        binding.historyViewModel = historyViewModel
    }

    override fun initView() {
        binding.rclHistory.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@HistoryActivity, 2)
            adapter = imageGeneratedAdapter
            CommonUtils.setClearAnimRecycleView(this)
        }
    }

    override fun listenLiveData() {
        lifecycleScope.launchWhenCreated {
            historyViewModel.listImageGenerated.collectLatest {
                imageGeneratedAdapter.submitData(it)
            }
        }
    }

    override fun listeners() {
        binding.btnSelect.setOnClickListener {
            historyViewModel.selectClick()
        }

        binding.btnShare.setOnClickListener {
            shareItemSelected()
        }

        binding.btnDelete.setOnClickListener {
            deleteItemSelected()
        }

        binding.btnBack.setOnClickListener {
            finishAction()
        }
    }

    private fun openHistory(imageGenerated: ImageGenerated) {
        startActivity(PreviewHistoryActivity.newInstance(this, imageGenerated.id))
    }

    private fun shareItemSelected() {

    }

    private fun deleteItemSelected() {
        DialogQuestionClearConversation(this, TypeCleanConversation.DELETE_IMAGE) {
            historyViewModel.deleteItemSelected()
        }.show()
    }

    private fun finishAction() {
        historyViewModel.stateSelect.get()?.let {
            if (it == StateSelect.Select) {
                finish()
            } else {
                historyViewModel.cancelSelect()
            }
        }
    }
}

enum class StateSelect {
    Select, SelectAll, DeselectAll
}