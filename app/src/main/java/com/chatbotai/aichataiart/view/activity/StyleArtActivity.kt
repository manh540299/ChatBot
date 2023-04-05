package com.chatbotai.aichataiart.view.activity

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.ActivityStyleArtBinding
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.PromptAdapter
import com.chatbotai.aichataiart.view.adapter.StyleArtAdapter
import com.chatbotai.aichataiart.view.dialog.DialogPleaseInstallFromStore
import com.chatbotai.aichataiart.viewmodel.StyleArtViewModel
import com.smarttech.smarttechlibrary.utils.CommonUtils

class StyleArtActivity : BaseActivityWithDataBiding<ActivityStyleArtBinding>(ActivityStyleArtBinding::inflate) {
    private val styleArtViewModel: StyleArtViewModel by viewModels()
    private var styleArt = -1
    private val promptAdapter = PromptAdapter {
        binding.edtPrompt.setText(it)
        binding.edtPrompt.setSelection(binding.edtPrompt.length())
        styleArtViewModel.selectPrompts(it)
    }

    private val styleArtAdapter = StyleArtAdapter {
        styleArt = it
        styleArtViewModel.selectArtStyle(it)
    }

    override fun isIgnoreTransparent(): Boolean {
        return true
    }

    override fun initData() {
        lightStatusBar()
        LogEventUtils.logFeature(UserFeature.CHOOSE_STYLE_ART)
    }

    override fun initView() {
        binding.rlPrompt.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@StyleArtActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = promptAdapter
        }

        binding.rlChooseArtStyle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@StyleArtActivity, 2, LinearLayoutManager.HORIZONTAL, false)
            adapter = styleArtAdapter
        }
    }

    override fun listenLiveData() {
        styleArtViewModel.getPrompts.observe(this) {
            promptAdapter.submitList(it)
        }

        styleArtViewModel.getArtStyle.observe(this) {
            Log.d("selectPrompts", "$it")
            styleArtAdapter.submitList(it)
        }
    }

    override fun listeners() {
        binding.edtPrompt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.isHaveText = count > 0
                if (count > 0) {
                    binding.edtPrompt.setBackgroundResource(R.drawable.bgr_edt_style)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.btnClearTextSearch.setOnClickListener {
            binding.edtPrompt.setText("")
        }

        binding.btnCreateArt.setOnClickListener {
            val prompt = binding.edtPrompt.text.toString()
            if (prompt.isEmpty() || prompt.isBlank()) {
                binding.tvWarning.setText(R.string.please_enter_prompt)
                binding.tvWarning.visibility = View.VISIBLE
                binding.edtPrompt.setBackgroundResource(R.drawable.bgr_edt_style_is_empty)
                binding.edtPrompt.setSelection(binding.edtPrompt.length())
            } else {
                binding.tvWarning.visibility = View.GONE
                clickCreate()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.edtPrompt.clearFocus()
        CommonUtils.hideKeyBroad(this, binding.edtPrompt)
    }

    private fun clickCreate() {
        if (!ManageSaveLocal.getAccessGenerateImage()) {
            showPayment(ShowPaymentFrom.GENERATE_IMAGE)
            return
        }
        if (!CommonUtils.verifyInstallerId(this) && !ManageSaveLocal.getIsAccessSendMessageFromGoogleStore()) {
            showDialogInstallFromStore()
            return
        }
        showAds {
            val style = if (styleArt == -1) "" else getString(styleArt)
            val message = if (style.isEmpty()) "${binding.edtPrompt.text}" else "${binding.edtPrompt.text} with style $style"
            Log.d("MessageDraw", message)
            startActivity(GenerateImageProcessActivity.newInstance(this, binding.edtPrompt.text.toString(), style))
            LogEventUtils.logFeature(UserFeature.CREATE_ART)
            finish()
        }
    }


    private fun showDialogInstallFromStore() {
        DialogPleaseInstallFromStore(this).show()
    }

    override fun purchaseSuccess() {
        super.purchaseSuccess()
        clickCreate()
    }
}
