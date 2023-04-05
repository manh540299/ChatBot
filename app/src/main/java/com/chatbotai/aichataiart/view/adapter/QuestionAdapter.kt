package com.chatbotai.aichataiart.view.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.database.model.QuestionGroupAndQuestion
import com.chatbotai.aichataiart.databinding.ItemQuestionBinding
import com.bumptech.glide.Glide

class QuestionAdapter(private val action: (Int, Boolean) -> Unit, private val needRefresh: () -> Unit) :
    PagingDataAdapter<QuestionGroupAndQuestion, QuestionAdapter.ViewHolder>(DiffQuestionGroupAndQuestion()) {
    class DiffQuestionGroupAndQuestion : DiffUtil.ItemCallback<QuestionGroupAndQuestion>() {
        override fun areContentsTheSame(oldItem: QuestionGroupAndQuestion, newItem: QuestionGroupAndQuestion): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: QuestionGroupAndQuestion, newItem: QuestionGroupAndQuestion): Boolean {
            return oldItem.questionGroup.nameGroup == newItem.questionGroup.nameGroup
        }
    }


    inner class ViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        private val adapterQues = QuestionChatAdapter(action, needRefresh)

        init {
            binding.rlQues.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = adapterQues
            }
        }

        fun bin(chat: QuestionGroupAndQuestion) {
            Glide.with(binding.imgTitle).load(chat.questionGroup.resIcon).into(binding.imgTitle)
            try {
                binding.txtTitle.setText(chat.questionGroup.nameGroup)
            } catch (_: Resources.NotFoundException) {
                needRefresh()
            }
            adapterQues.submitList(chat.listQuestion)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bin(it)
        }
    }
}