package com.chatbotai.aichataiart.view.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chatbotai.aichataiart.view.activity.TutorialActivity
import com.chatbotai.aichataiart.view.fragment.TutorialFragment

class TutorialPagerAdapter(fragmentActivity: FragmentActivity, private val list: ArrayList<TutorialActivity.Tutorial>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return TutorialFragment().apply {
            val bundle = Bundle()
            bundle.putSerializable("tutorial", list[position])
            arguments = bundle
        }
    }
}