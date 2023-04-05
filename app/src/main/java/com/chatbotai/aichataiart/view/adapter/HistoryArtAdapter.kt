package com.chatbotai.aichataiart.view.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.databinding.ItemImageGeneratedBinding
import com.chatbotai.aichataiart.utils.AnimUtils
import com.smarttech.smarttechlibrary.utils.CommonUtils

class HistoryArtAdapter(private val action: (ImageGenerated) -> Unit) :
    PagingDataAdapter<ImageGenerated, HistoryArtAdapter.ViewHolder>(DiffImageGenerated()) {
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
            ItemImageGeneratedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(private val binding: ItemImageGeneratedBinding) : RecyclerView.ViewHolder(binding.root), ImageGeneratedHandleClick {
        init {
            val size = (Resources.getSystem().displayMetrics.widthPixels - CommonUtils.dpToPx(48f)).toInt() / 2
            val param = binding.imgHistory.layoutParams as ConstraintLayout.LayoutParams
            param.width = size
            param.height = size
            binding.imgHistory.layoutParams = param
            binding.imageGeneratedHandleClick = this
            AnimUtils.boundView(binding.root)
        }

        fun bind(imageGenerated: ImageGenerated) {
            binding.imageGenerated = imageGenerated
        }

        override fun itemClick(imageGenerated: ImageGenerated) {
            action(imageGenerated)
        }

//        override fun itemLongClick(view: View, id : Long): Boolean {
//            actionLongClick(id)
//            return false
//        }
    }
}

interface ImageGeneratedHandleClick {
    fun itemClick(imageGenerated: ImageGenerated)
//    fun itemLongClick(view: View, id: Long): Boolean
}