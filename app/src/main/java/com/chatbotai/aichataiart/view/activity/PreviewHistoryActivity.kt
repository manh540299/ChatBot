package com.chatbotai.aichataiart.view.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.chatbotai.aichataiart.databinding.ActivityPreviewHistoryBinding
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.dialog.DialogQuestionClearConversation
import com.chatbotai.aichataiart.view.dialog.TypeCleanConversation
import com.chatbotai.aichataiart.viewmodel.PreviewHistoryViewModel
import kotlinx.coroutines.launch

class PreviewHistoryActivity : BaseActivityWithDataBiding<ActivityPreviewHistoryBinding>(ActivityPreviewHistoryBinding::inflate) {
    private val previewHistoryViewModel: PreviewHistoryViewModel by viewModels()

    companion object {
        fun newInstance(context: Context, id: Long): Intent {
            return Intent(context, PreviewHistoryActivity::class.java).apply {
                putExtra("id", id)
            }
        }

        private fun receiveData(intent: Intent, result: (Long) -> Unit) {
            result(intent.getLongExtra("id", -1L))
        }
    }

    override fun initData() {
        lightStatusBar()
        marginStatusBar(listOf(binding.btnBack))
        marginNavigationBar(listOf(binding.btnShare))
        receiveData(intent) {
            previewHistoryViewModel.setId(it)
            previewHistoryViewModel.imageGenerated.observe(this) { imageGenerate ->
                Log.d("imageGenerated", "$imageGenerate")
                imageGenerate?.let {
                    binding.t1.text = imageGenerate.contentDraw
                    loadImage(imageGenerate.path, binding.imgPreview)
//                    binding.imgPreview.setImage(ImageSource.uri(imageGenerate.path))
                }
            }
        }
    }

    override fun initView() {

    }

    override fun listenLiveData() {
    }

    override fun listeners() {
        binding.imgPreview.setOnClickListener {
            binding.isShowFullScreen = !(binding.isShowFullScreen ?: false)
        }

        binding.btnShare.setOnClickListener {
            share()
        }

        binding.btnDelete.setOnClickListener {
            delete()
        }

        binding.btnBack.setOnClickListener {
            finishAction()
        }
    }

    private fun share() {
        lifecycleScope.launch {
            val path = previewHistoryViewModel.getPath()
            Log.d("getPath", path)
            CommonUtils.shareCollect(this@PreviewHistoryActivity, listOf(path))
        }
    }

    private fun delete() {
        DialogQuestionClearConversation(this, TypeCleanConversation.DELETE_IMAGE) {
            previewHistoryViewModel.delete {
                finish()
            }
        }.show()
    }

    private fun finishAction() {
        finish()
    }
}