package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.DialogSelectModeBinding
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.view.dialog.base.BaseDialogDataBinding

class DialogSelectMode(context: Context) : BaseDialogDataBinding<DialogSelectModeBinding>(context, DialogSelectModeBinding::inflate, R.style.DefaultDialog) {
    override fun initView() {
        super.initView()
        binding.isSelectSmart = ManageSaveLocal.isSmartOrAdvance()
    }

    override fun listener() {
        super.listener()
        binding.btnSelectSmart.setOnClickListener {
            binding.isSelectSmart = true
            ManageSaveLocal.setIsSmartOrAdvance(true)
        }

        binding.btnSelectAdvance.setOnClickListener {
            binding.isSelectSmart = false
            ManageSaveLocal.setIsSmartOrAdvance(false)
        }

        binding.btnOk.setOnClickListener {
            dismiss()
        }
    }
}