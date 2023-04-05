package com.chatbotai.aichataiart.view.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.databinding.ActivityGenerateImageProcessBinding
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.dialog.DialogQuestionStopGenerateImage
import com.chatbotai.aichataiart.viewmodel.GenerateImageProcessViewModel

class GenerateImageProcessActivity : BaseActivityWithDataBiding<ActivityGenerateImageProcessBinding>(ActivityGenerateImageProcessBinding::inflate), GenerateHandleClick {
    private val generateImageProcessViewModel: GenerateImageProcessViewModel by viewModels()

    companion object {
        fun newInstance(context: Context, message: String, style: String): Intent {
            return Intent(context, GenerateImageProcessActivity::class.java).apply {
                putExtra("message", message)
                putExtra("style", style)
            }
        }

        private fun receiveData(intent: Intent, result: (String?, String?) -> Unit) {
            result(intent.getStringExtra("message"), intent.getStringExtra("style"))
        }
    }

    override fun isIgnoreTransparent(): Boolean {
        return true
    }

    override fun initData() {
        lightStatusBar()
        binding.generateImageProcessViewModel = generateImageProcessViewModel
        binding.generateHandleClick = this

        receiveData(intent) { message, style ->
            message?.let {
                val message = if (style?.isEmpty() == true) it else "$it $style"
                generateImageProcessViewModel.generateImage(message)
            }
        }
    }

    override fun initView() {
    }

    override fun listenLiveData() {
        generateImageProcessViewModel.resultGenerateImage.observe(this) {
            Log.d("resultGenerateImage", "$it")

            it.message.let { listMessage ->
                receiveData(intent) { message, style ->
                    message?.let {
                        startActivity(PhotoGeneratedActivity.newInstance(this, listMessage, message, style ?: ""))
                    }
                }
                LogEventUtils.logFeature(UserFeature.CREATE_ART_SUCCESS)
                finish()
            }
        }
    }

    override fun listeners() {
        binding.btnBack.setOnClickListener {
            finishAction()
        }

        onBackPressedDispatcher.addCallback {
            finishAction()
        }
    }

    private fun finishAction() {
        finish()
    }

    override fun stopClick(networkState: NetworkState?) {
        networkState?.let {
            if (it.status == NetworkState.State.LOADING) {
                showDialogStopGenerate()
            } else {
                retryGenerate()
            }
        }
    }

    private fun retryGenerate() {
        generateImageProcessViewModel.generateImage()
    }

    private fun showDialogStopGenerate() {
        DialogQuestionStopGenerateImage(this) {
            finishAction()
        }.show()
    }

}

interface GenerateHandleClick {
    fun stopClick(networkState: NetworkState?)
}
