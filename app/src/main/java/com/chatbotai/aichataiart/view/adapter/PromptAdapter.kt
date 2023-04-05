package com.chatbotai.aichataiart.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.databinding.ItemPromptBinding
import com.chatbotai.aichataiart.model.Prompt

class PromptAdapter(private val action: (Int) -> Unit) : ListAdapter<Prompt, PromptAdapter.ViewHolder>(DiffPrompt()) {
    inner class ViewHolder(private val binding: ItemPromptBinding) : RecyclerView.ViewHolder(binding.root), PromptHandleClick {
        init {
            binding.promptHandleClick = this
        }

        fun bin(prompt: Prompt) {
            binding.prompt = prompt
            binding.tvPrompt.setText(prompt.text)
        }

        override fun itemClick(text: Int) {
            action(text)
        }
    }

    class DiffPrompt : DiffUtil.ItemCallback<Prompt>() {
        override fun areItemsTheSame(oldItem: Prompt, newItem: Prompt): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: Prompt, newItem: Prompt): Boolean {
            return oldItem.check == newItem.check
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPromptBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bin(getItem(position))
    }
}

interface PromptHandleClick {
    fun itemClick(text: Int)
}