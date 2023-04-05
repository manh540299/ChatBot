package com.chatbotai.aichataiart.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.databinding.ItemHistoryArtMainBinding
import com.chatbotai.aichataiart.utils.AnimUtils

class HistoryArtMainAdapter(private val action: (Long) -> Unit) :
    PagingDataAdapter<ImageGenerated, HistoryArtMainAdapter.ViewHolder>(DiffImageGenerated()) {
    class DiffImageGenerated : DiffUtil.ItemCallback<ImageGenerated>() {
        override fun areContentsTheSame(oldItem: ImageGenerated, newItem: ImageGenerated): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: ImageGenerated, newItem: ImageGenerated): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryArtMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(private val binding: ItemHistoryArtMainBinding) : RecyclerView.ViewHolder(binding.root), ImageGeneratedHandleClick {
        init {
            binding.imageGeneratedHandleClick = this
            AnimUtils.boundView(binding.root)
        }

        fun bind(imageGenerated: ImageGenerated) {
            binding.imageGenerated = imageGenerated
        }

        override fun itemClick(imageGenerated: ImageGenerated) {
            action(imageGenerated.id)
        }
    }
}