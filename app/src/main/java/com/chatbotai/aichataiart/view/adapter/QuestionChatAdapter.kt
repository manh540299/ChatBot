package com.chatbotai.aichataiart.view.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.database.model.Question
import com.chatbotai.aichataiart.databinding.ItemQuestionTextBinding

class QuestionChatAdapter(private val action: (Int, Boolean) -> Unit, private val needRefresh: () -> Unit) : ListAdapter<Question, QuestionChatAdapter.ViewHolder>(DiffQuestionGroupAndQuestion()) {
    class DiffQuestionGroupAndQuestion : DiffUtil.ItemCallback<Question>() {
        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.question == newItem.question
        }
    }

    inner class ViewHolder(private val binding: ItemQuestionTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bin(chat: Question) {
            if (chat.check) {
                binding.btnQues.background.setTint(ContextCompat.getColor(binding.root.context, R.color.green_light))
            } else {
                binding.btnQues.background.setTint(ContextCompat.getColor(binding.root.context, R.color.grey_b))
            }
            binding.btnQues.apply {
                try {
                    setText(chat.question)
                } catch (_: Resources.NotFoundException) {
                    needRefresh()
                }
                setOnClickListener {
                    action(chat.question, chat.check)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemQuestionTextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bin(it)
        }
    }
}