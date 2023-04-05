package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import android.widget.Toast
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.DialogRateBinding
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.view.dialog.base.BaseDialog
import com.smarttech.smarttechlibrary.utils.CommonUtils

class RateDialog(context: Context, private val action: (() -> Unit)? = null) : BaseDialog<DialogRateBinding>(context, DialogRateBinding::inflate, themeResId = R.style.DefaultDialog) {

    companion object {
        var isShowFirst = false
    }

    override fun show() {
        super.show()
        isShowFirst = true
    }

    override fun initData() {

    }

    override fun listener() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.simpleRatingBar.setOnRatingChangeListener { _, rating, _ ->
            binding.btnRate.alpha = if (rating > 0) 1f else 0.7f
        }

        binding.btnRate.setOnClickListener {
            val rate = binding.simpleRatingBar.rating.toInt()
            if (rate == 0) {
                return@setOnClickListener
            } else if (rate >= 4) {
                ManageSaveLocal.setIsRating(true)
                CommonUtils.ratingInApp(context, object : CommonUtils.CallBackRating {
                    override fun fail() {
                        CommonUtils.goToChPlay(context)
                    }

                    override fun success() {
                        Toast.makeText(context, context.getString(R.string.thank_feed_back), Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                DialogFeedBack(context, action).show()
                dismiss()
            }
        }

        binding.tvLater.setOnClickListener {
            dismiss()
            action?.let {
                it()
            }
        }
    }
}