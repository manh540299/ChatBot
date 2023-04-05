package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import android.widget.Toast
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.DialogGetMessageBinding
import com.chatbotai.aichataiart.view.dialog.base.BaseDialogDataBinding
import com.smarttech.smarttechlibrary.ads.RewardedAdsManager

class DialogGetMessage(context: Context, private val action: (Boolean) -> Unit) : BaseDialogDataBinding<DialogGetMessageBinding>(context, DialogGetMessageBinding::inflate, R.style.DefaultDialog) {
    override fun initView() {
        super.initView()
    }

    override fun listener() {
        super.listener()
        binding.btnGetUnlimited.setOnClickListener {
            action(true)
        }

        binding.btnWatchAds.setOnClickListener {
            binding.isLoadingAds = true
            RewardedAdsManager.loadAd(context) { loadSuccess ->
                binding.isLoadingAds = false
                if (loadSuccess) {
                    action(false)
                    dismiss()
                } else {
                    Toast.makeText(context, context.getString(R.string.no_ads_download), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }


}