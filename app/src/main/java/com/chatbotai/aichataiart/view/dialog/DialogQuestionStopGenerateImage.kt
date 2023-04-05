package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import com.chatbotai.aichataiart.databinding.DialogQuestionGenerateImageBinding
import com.chatbotai.aichataiart.view.dialog.base.BaseDialog

class DialogQuestionStopGenerateImage(context: Context, private val action: () -> Unit) : BaseDialog<DialogQuestionGenerateImageBinding>(context, DialogQuestionGenerateImageBinding::inflate) {

    override fun listener() {
        super.listener()
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnExit.setOnClickListener {
            dismiss()
            action()
        }
    }
}