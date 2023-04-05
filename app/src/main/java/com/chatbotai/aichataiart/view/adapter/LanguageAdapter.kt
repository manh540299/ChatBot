package com.chatbotai.aichataiart.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.ItemLanguageBinding
import com.chatbotai.aichataiart.model.Language
import com.bumptech.glide.Glide

class LanguageAdapter(private val action: (Int) -> Unit) : ListAdapter<Language, LanguageAdapter.ViewHolder>(DiffLanguage()) {

    class DiffLanguage : DiffUtil.ItemCallback<Language>() {
        override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem == newItem
        }

    }

    inner class ViewHolder(private val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bin(language: Language, position: Int, size: Int) {
            if (position == size - 1) {
                binding.line.visibility = View.GONE
                binding.root.setBackgroundResource(R.drawable.ripple_bot_language)
            } else
                binding.line.visibility = View.VISIBLE
            Glide.with(binding.imgLanguage).load(language.image).into(binding.imgLanguage)
            binding.tvName.setText(language.name)
            if (language.choose)
                binding.imgTick.visibility = View.VISIBLE
            else
                binding.imgTick.visibility = View.GONE
            if (position == 0)
                binding.root.setBackgroundResource(R.drawable.ripple_top_language)
            binding.root.setOnClickListener {
                action(language.name)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bin(getItem(position), position, itemCount)
    }
}