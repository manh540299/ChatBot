package com.chatbotai.aichataiart.view.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.databinding.ActivityDetailConversationBinding
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.utils.Constants.GPT_TYPING
import com.chatbotai.aichataiart.utils.Constants.GPT_TYPING_DEFAULT
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.ContentConversationAdapter
import com.chatbotai.aichataiart.view.custom.PopupMenuCustomLayout
import com.chatbotai.aichataiart.view.dialog.*
import com.chatbotai.aichataiart.viewmodel.DetailConversationViewModel
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.smarttech.smarttechlibrary.ads.RewardedAdsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.*


class DetailConversationActivity : BaseActivityWithDataBiding<ActivityDetailConversationBinding>(ActivityDetailConversationBinding::inflate), OnUserEarnedRewardListener {
    private val detailConversationViewModel: DetailConversationViewModel by viewModels()
    private val contentConversationAdapter: ContentConversationAdapter by lazy {
        ContentConversationAdapter {
            when (it) {
                is Long -> {
                    DialogQuestionClearConversation(this, TypeCleanConversation.MESSAGE) {
                        detailConversationViewModel.deleteMessage(it)
                    }.show()
                }
            }
        }
    }

    private var mActivityResultQuestion: ActivityResultLauncher<Intent>? = null
    private lateinit var textToSpeech: TextToSpeech
    private var ready = false

    private fun createActivityLauncher() {
        mActivityResultQuestion = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(javaClass.simpleName, "resultCode=${it.resultCode}")
            when (it.resultCode) {
                RESULT -> {
                    it.data?.getStringExtra("message")?.let { message ->
                        sendQuestion(message)
                    }
                }
            }
        }
    }

    override fun isIgnoreTransparent(): Boolean {
        return true
    }

    companion object {
        const val RESULT = 234
        const val REQ_CODE_SPEECH_INPUT = 100
        fun newInstance(context: Context, idConversation: Long): Intent {
            return Intent(context, DetailConversationActivity::class.java).apply {
                putExtra("id", idConversation)
            }
        }

        fun newInstance(context: Context, message: String): Intent {
            return Intent(context, DetailConversationActivity::class.java).apply {
                putExtra("message", message)
            }
        }

        private fun receiveData(intent: Intent, result: (Long) -> Unit) {
            result(intent.getLongExtra("id", -1L))
        }

        private fun receiveMessageData(intent: Intent, result: (String?) -> Unit) {
            result(intent.getStringExtra("message"))
        }
    }

    override fun initData() {
        createActivityLauncher()
        receiveData(intent) { id: Long ->
            if (id != -1L) {
                detailConversationViewModel.setId(id)
            } else {
                receiveMessageData(intent) { message: String? ->
                    message?.let {
                        detailConversationViewModel.sendQuestion(it)
                    }
                }
            }
        }
        updateNumberRemaining()
        binding.detailConversationViewModel = detailConversationViewModel
        textToSpeech = TextToSpeech(this) {
            when (it) {
                TextToSpeech.SUCCESS -> {
                    Log.d("TextToSpeech", "SUCCESS")
                    ready = true
                }
                TextToSpeech.ERROR -> {
                    Log.d("TextToSpeech", "ERROR")
                    ready = false
                }
            }
        }

    }

    private fun updateNumberRemaining() {
        binding.numberRemaining = ManageSaveLocal.getNumberMessageRemaining()
    }

    override fun initView() {
        lightStatusBar()
        binding.isOnSpeaker = ManageSaveLocal.getIsOnSpeaker()
        binding.rclConversation.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DetailConversationActivity, LinearLayoutManager.VERTICAL, true)
            adapter = contentConversationAdapter
        }
    }

    override fun listenLiveData() {
        lifecycleScope.launchWhenCreated {
            detailConversationViewModel.listConversations.collectLatest {
                contentConversationAdapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            contentConversationAdapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }.collect {
                    binding.rclConversation.smoothScrollToPosition(0)
                }
        }

        detailConversationViewModel.networkStateSendQuestion.observe(this) {
            Log.d("networkStateSend", "$it")
            if (it.status == NetworkState.State.FAILED) {
                detailConversationViewModel.updateMessageDefaultBot()
            }
        }

        detailConversationViewModel.resultMessage.observe(this) {
            speakOut(it)
            updateNumberRemaining()
        }
    }

    override fun listeners() {
        binding.btnRemainingMessage.setOnClickListener {
            showDialogGetMessage()
        }

        binding.edtQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.isHaveTextToSend = (count > 0 && s.toString().isNotEmpty() && s.toString().isNotBlank())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.isHaveText = (count > 0)
                detailConversationViewModel.search(s.toString())
                contentConversationAdapter.refresh()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.btnSend.setOnClickListener {
            if (!ManageSaveLocal.getAccessSendMessage()) {
                if (ManageSaveLocal.getIsShowAdsRewarded()) {
                    showDialogGetMessage()
                } else {
                    showPayment(ShowPaymentFrom.DETAIL_CONVERSATION)
                }
                return@setOnClickListener
            }
            sendQuestion()
        }

        binding.btnMenu.setOnClickListener {
            PopupMenuCustomLayout(this, R.layout.layout_menu_top_detail_conversation) { menuItemId ->
                Log.d("PopupMenuCustomLayout", "$menuItemId")
                when (menuItemId) {
                    R.id.btnSelectMode -> {
                        DialogSelectMode(this).show()
                    }
                    R.id.btnTipsQuestion -> {
                        TipsQuestionActivity.newInstance(this, true).apply {
                            mActivityResultQuestion?.launch(this)
                        }
                    }
                    R.id.btnFindMessage -> {
                        binding.isSearching = true
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(200)
                            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                            binding.edtSearch.requestFocus()
                            com.smarttech.smarttechlibrary.utils.CommonUtils.showKeyBroad(this@DetailConversationActivity, binding.edtSearch)
                        }
                    }
                    R.id.btnClearHistory -> {
                        DialogQuestionClearConversation(this, TypeCleanConversation.HISTORY) {
                            detailConversationViewModel.deleteHistory()
                        }.show()
                    }
                }
            }.showMenuTop(it)
        }

        binding.btnSpeaker.setOnClickListener {
            binding.isOnSpeaker = !(binding.isOnSpeaker ?: true)
            ManageSaveLocal.setIsOnSpeaker(binding.isOnSpeaker ?: true)
            if (binding.isOnSpeaker == false) {
                textToSpeech.stop()
            }
        }

        binding.btnShare.setOnClickListener {
            if (CommonUtils.checkPermission(this)) {
                share()
            } else {
                allowPermission {
                    share()
                }
            }
        }

        binding.btnMicrophone.setOnClickListener {
            promptSpeechInput()
        }

        binding.btnClearTextSearch.setOnClickListener {
            binding.edtSearch.setText("")
        }

        binding.btnCancel.setOnClickListener {
            com.smarttech.smarttechlibrary.utils.CommonUtils.hideKeyBroad(this, binding.edtSearch)
            finishAction()
        }

        binding.btnBack.setOnClickListener {
            finishAction()
        }

        onBackPressedDispatcher.addCallback {
            finishAction()
        }
    }

    private fun showDialogGetMessage() {
        DialogGetMessage(this) {
            if (it) {
                showPayment(ShowPaymentFrom.DETAIL_CONVERSATION)
            } else {
                RewardedAdsManager.showAds(this, this)
            }
        }.show()
    }

    private fun sendQuestion(message: String? = null) {
        if (!com.smarttech.smarttechlibrary.utils.CommonUtils.verifyInstallerId(this) && !ManageSaveLocal.getIsAccessSendMessageFromGoogleStore()) {
            showDialogInstallFromStore()
        } else {
            detailConversationViewModel.sendQuestion(message ?: binding.edtQuestion.text.toString())
            binding.edtQuestion.setText("")
        }
    }

    private fun share() {
        com.smarttech.smarttechlibrary.utils.CommonUtils.hideKeyBroad(this, binding.edtQuestion)
        lifecycleScope.launch {
            delay(200)
            val bitmap = binding.rclConversation.drawToBitmap()
            CommonUtils.sharePalette(bitmap, this@DetailConversationActivity)
            bitmap.recycle()
        }
    }

    private fun showDialogInstallFromStore() {
        DialogPleaseInstallFromStore(this).show()
    }


    private fun promptSpeechInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show()
        } catch (a: SecurityException) {
            Toast.makeText(this, getString(R.string.permission_denial), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CODE_SPEECH_INPUT)
            when (resultCode) {
                RESULT_OK -> {
                    data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let {
                        var textResult = ""
                        for (i in it) {
                            textResult += " $i"
                        }
                        Log.d("SpeechToText", textResult)
                        textResult = "${binding.edtQuestion.text}$textResult"
                        binding.edtQuestion.setText(textResult)
                        binding.edtQuestion.setSelection(textResult.length)
                    }
                }
                RecognizerIntent.RESULT_AUDIO_ERROR -> {
                    showToastMessage("Audio Error")
                }
                RecognizerIntent.RESULT_CLIENT_ERROR -> {
                    showToastMessage("Client Error")
                }
                RecognizerIntent.RESULT_NETWORK_ERROR -> {
                    showToastMessage("Network Error")
                }
                RecognizerIntent.RESULT_NO_MATCH -> {
                    showToastMessage("No Match")
                }
                RecognizerIntent.RESULT_SERVER_ERROR -> {
                    showToastMessage("Server Error")
                }
            }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showToastMessage(s: String) {
        Log.d("SpeechToText", "Error: $s")
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }


    private fun speakOut(message: String) {
        if (binding.isOnSpeaker == false) {
            return
        }

        if (!ready) {
            Toast.makeText(this, "Text to Speech not ready", Toast.LENGTH_SHORT).show()
            return
        }
        textToSpeech.stop()
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }

    private fun finishAction() {
        if (binding.isSearching == true) {
            binding.isSearching = false
            binding.edtSearch.setText("")
            return
        } else if (!RateDialog.isShowFirst && !ManageSaveLocal.getIsRating()) {
            RateDialog(this) {
                finish()
            }.show()
        } else {
            finish()
        }
    }


    override fun onPause() {
        super.onPause()
        textToSpeech.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        ContentConversationAdapter.isFirstLoadChat = false
        mActivityResultQuestion = null
        GPT_TYPING = GPT_TYPING_DEFAULT
    }

    override fun purchaseSuccess() {
        super.purchaseSuccess()
        sendQuestion()
    }

    override fun onSaveState(outState: Bundle) {
        super.onSaveState(outState)
        detailConversationViewModel.saveDataStore()
    }

    override fun onRestoreState(savedInstanceState: Bundle) {
        super.onRestoreState(savedInstanceState)
        detailConversationViewModel.restoreData()
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        ManageSaveLocal.setCountRewardSendMessage()
        updateNumberRemaining()
    }
}