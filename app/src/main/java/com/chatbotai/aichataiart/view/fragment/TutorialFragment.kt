package com.chatbotai.aichataiart.view.fragment

import android.os.Build
import android.util.Log
import com.chatbotai.aichataiart.databinding.FragmentTutorialBinding
import com.chatbotai.aichataiart.view.activity.TutorialActivity
import com.chatbotai.aichataiart.view.fragment.base.BaseFragment

class TutorialFragment : BaseFragment<FragmentTutorialBinding>(FragmentTutorialBinding::inflate) {
    override fun initData() {
        
    }

    override fun initView() {
        marginViewWithStatusBar(listOf( binding.txtTitle))
        requireArguments().let {
            val tutorial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable("tutorial", TutorialActivity.Tutorial::class.java)
            } else {
                it.getSerializable("tutorial") as? TutorialActivity.Tutorial
            }
            binding.tutorial = tutorial
        }
        
    }

    override fun listenLiveData() {
        
    }

    override fun listener() {
        
    }

}