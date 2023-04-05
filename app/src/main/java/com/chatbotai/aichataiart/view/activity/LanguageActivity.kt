package com.chatbotai.aichataiart.view.activity

import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.ActivityLanguageBinding
import com.chatbotai.aichataiart.model.Language
import com.chatbotai.aichataiart.utils.LanguageUtil
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.LanguageAdapter
import com.chatbotai.aichataiart.viewmodel.LanguageViewModel

class LanguageActivity : BaseActivityWithDataBiding<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {

    private val languageViewModel: LanguageViewModel by viewModels()
    private val languageAdapter = LanguageAdapter {
        if (it != R.string.english) {
            Toast.makeText(this, getString(R.string.voice_coming_soon), Toast.LENGTH_SHORT).show()
        } else {
            setLanguage(it)
            ManageSaveLocal.setLanguage(it)
        }
    }

    override fun initData() {
        lightStatusBar()
        setLanguage(ManageSaveLocal.getLanguage())
    }

    override fun initView() {
        marginStatusBar(listOf(binding.btnBack))
        paddingNavigationBar(listOf(binding.rlLanguage))
        binding.rlLanguage.apply {
            layoutManager = LinearLayoutManager(this@LanguageActivity, LinearLayoutManager.VERTICAL, false)
            adapter = languageAdapter
        }
    }

    override fun listenLiveData() {
        languageViewModel.getLanguage().observe(this) {
            Log.d("azi", it.toString())
            languageAdapter.submitList(it)
        }

    }

    override fun listeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun setLanguage(name: Int) {
        val languages = mutableListOf<Language>()
        languages.addAll(LanguageUtil.language)
        for (i in languages.indices) {
            if (name == LanguageUtil.language[i].name)
                languages[i] = Language(languages[i].image, languages[i].name, true)
        }
        languageViewModel.setLanguage(languages)
    }
}