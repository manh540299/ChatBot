package com.chatbotai.aichataiart.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.databinding.ItemArtStyleBinding
import com.chatbotai.aichataiart.model.ArtStyle
import com.github.hariprasanths.bounceview.BounceView

class StyleArtAdapter(private val action: (Int) -> Unit) : ListAdapter<ArtStyle, StyleArtAdapter.ViewHolder>(
    DiffPrompt()
) {
    class DiffPrompt : DiffUtil.ItemCallback<ArtStyle>() {
        override fun areItemsTheSame(oldItem: ArtStyle, newItem: ArtStyle): Boolean {
            return newItem.text == oldItem.text
        }

        override fun areContentsTheSame(oldItem: ArtStyle, newItem: ArtStyle): Boolean {
            return newItem.check == oldItem.check
        }
    }

    override fun submitList(list: List<ArtStyle>?) {
        super.submitList(ArrayList<ArtStyle>(list ?: listOf()))
    }

    inner class ViewHolder(private val binding: ItemArtStyleBinding) : RecyclerView.ViewHolder(binding.root), StyleArtHandleClick {
        init {
            BounceView.addAnimTo(itemView).setScaleForPushInAnim(1.02f, 1.02f).setScaleForPopOutAnim(1.02f, 1.02f)
            binding.styleArtHandleClick = this
        }

        fun bind(artStyle: ArtStyle) {
            binding.artStyle = artStyle
        }

        override fun itemClick(text: Int) {
            action(text)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

interface StyleArtHandleClick {
    fun itemClick(text: Int)
}