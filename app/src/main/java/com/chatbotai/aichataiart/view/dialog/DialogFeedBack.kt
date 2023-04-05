package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.DialogFeedBackBinding
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.view.dialog.base.BaseDialog
import com.smarttech.smarttechlibrary.utils.ToastCustom


class DialogFeedBack(context: Context, private val action: (() -> Unit)? = null) : BaseDialog<DialogFeedBackBinding>(context, DialogFeedBackBinding::inflate, themeResId = R.style.DefaultDialog) {
    companion object {
        fun newInstance(context: Context, action: (() -> Unit)? = null) {
            DialogFeedBack(context, action).show()
        }

        var isShow = false
    }

    override fun show() {
        if (!isShow) {
            isShow = true
            super.show()
        }
    }

    override fun dismiss() {
        super.dismiss()
        isShow = false
    }

    override fun initData() {
    }

    override fun listener() {
        binding.btnLater.setOnClickListener {
            cancel()
            action?.let {
                it()
            }
        }

        binding.btnSendFeedback.setOnClickListener {
            val content = binding.edtContentFeedBack.text.toString()
            if (content.isEmpty()) {
                return@setOnClickListener
            }
            LogEventUtils.logFeedBack(content)
            ToastCustom.makeText(context, context.getString(R.string.thank_feed_back))
            ManageSaveLocal.setIsRating(true)
            cancel()
            action?.let {
                it()
            }
        }
    }
}