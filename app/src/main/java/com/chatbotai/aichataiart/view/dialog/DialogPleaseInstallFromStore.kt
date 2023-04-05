package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.DialogPleaseInstallFromStoreBinding
import com.chatbotai.aichataiart.view.dialog.base.BaseDialogDataBinding
import com.smarttech.smarttechlibrary.utils.CommonUtils

class DialogPleaseInstallFromStore(context: Context) : BaseDialogDataBinding<DialogPleaseInstallFromStoreBinding>(context, DialogPleaseInstallFromStoreBinding::inflate, R.style.DefaultDialog) {
    override fun initView() {
        super.initView()
    }

    override fun listener() {
        super.listener()
        binding.btnInstallNow.setOnClickListener {
            CommonUtils.goToChPlay(context)
        }

        binding.btnLater.setOnClickListener {
            dismiss()
        }
    }
}