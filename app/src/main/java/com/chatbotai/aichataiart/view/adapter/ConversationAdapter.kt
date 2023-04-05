package com.chatbotai.aichataiart.view.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.database.model.Conversation
import com.chatbotai.aichataiart.databinding.ItemAdsNativeBinding
import com.chatbotai.aichataiart.databinding.ItemConversationBinding
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.utils.TimeUtils
import com.chatbotai.aichataiart.view.custom.PopupMenuCustomLayout
import com.chatbotai.aichataiart.viewmodel.ItemConversationAdsModel
import com.smarttech.smarttechlibrary.ads.AdsInterstitialManager

class ConversationAdapter(private val action: (Any) -> Unit, private val actionShowAds: (Boolean) -> Unit) : PagingDataAdapter<ItemConversationAdsModel, RecyclerView.ViewHolder>(DiffConversation()) {
    class DiffConversation : DiffUtil.ItemCallback<ItemConversationAdsModel>() {
        override fun areContentsTheSame(oldItem: ItemConversationAdsModel, newItem: ItemConversationAdsModel): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: ItemConversationAdsModel, newItem: ItemConversationAdsModel): Boolean {
            val isSameRepoItem = oldItem is ItemConversationAdsModel.ItemConversation
                    && newItem is ItemConversationAdsModel.ItemConversation
                    && oldItem.tray.id == newItem.tray.id

            val isSameSeparatorItem = oldItem is ItemConversationAdsModel.ItemAds
                    && newItem is ItemConversationAdsModel.ItemAds

            return isSameRepoItem || isSameSeparatorItem
        }
    }


    // 0: have time
    // 1: no time
    // 2: Ads

    override fun getItemViewType(position: Int): Int {
        peek(position)?.let {
            return when (it) {
                is ItemConversationAdsModel.ItemConversation -> {
                    if (position == 0) {
                        return 0
                    } else {
                        if (position == 1) {
                            peek(0)?.let {
                                if (it is ItemConversationAdsModel.ItemAds) {
                                    return 0
                                }
                            }
                        } else if (TimeUtils.isSameDay(it.tray.time)) {
                            return 1
                        } else {
                            peek(position - 1)?.let { oldItem ->
                                if (oldItem is ItemConversationAdsModel.ItemConversation) {
                                    return if (checkViewTypeHaveTime(it.tray, oldItem.tray)) {
                                        0
                                    } else {
                                        1
                                    }
                                } else {
                                    return 0
                                }
                            }
                        }
                    }
                    return 0
                }
                is ItemConversationAdsModel.ItemAds -> {
                    2
                }
            }
        }
//
//        peek(position)?.let {
//            if (position == 0) {
//                return 0
//            } else {
//                if (TimeUtils.isSameDay(it.time)) {
//                    return 1
//                } else {
//                    peek(position - 1)?.let { oldItem ->
//                        return if (checkViewTypeHaveTime(it, oldItem)) {
//                            0
//                        } else {
//                            1
//                        }
//                    }
//                }
//            }
//        }
        return 0
    }

    private fun checkViewTypeHaveTime(newItem: Conversation, oldItem: Conversation): Boolean {
        return TimeUtils.isYesterday(newItem.time, oldItem.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 2) {
            AdsViewHolder(ItemAdsNativeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ViewHolder(
                ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false), action
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdsViewHolder -> {
                holder.bind()
            }
            is ViewHolder -> {
                getItem(position)?.let {
                    if (it is ItemConversationAdsModel.ItemConversation) {
                        holder.bind(it.tray, viewType = getItemViewType(position))
                    }
                }
            }
        }
    }

    class ViewHolder(private val binding: ItemConversationBinding, private val action: (Any) -> Unit) : RecyclerView.ViewHolder(binding.root), ConversationHandleClick {
        init {
            val param = binding.txtNameConversation.layoutParams as ConstraintLayout.LayoutParams
            param.matchConstraintMaxWidth = (Resources.getSystem().displayMetrics.widthPixels - com.smarttech.smarttechlibrary.utils.CommonUtils.dpToPx(100f)).toInt()
            binding.txtNameConversation.layoutParams = param

            binding.conversationHandleClick = this
        }

        fun bind(conversation: Conversation, viewType: Int) {
            if (viewType == 0) {
                binding.time = TimeUtils.getTimeHistoryConversation(conversation.time, itemView.context)
            } else {
                binding.time = ""
            }
            binding.conversation = conversation
        }

        override fun itemClick(id: Long) {
            action(id)
        }

        override fun itemLongClick(view: View, conversation: Conversation): Boolean {
            PopupMenuCustomLayout(view.context, R.layout.layout_menu_detail_conversation) { menuItemId ->
                when (menuItemId) {
                    R.id.btnCopy -> {
                        CommonUtils.copyClipboard(itemView.context, conversation.nameConversation)
                    }
                    R.id.btnDelete -> {
                        action(conversation)
                    }
                }
            }.show(view)
            return true
        }
    }


    inner class AdsViewHolder(private val binding: ItemAdsNativeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            actionShowAds(false)
//            AdsInterstitialManager.loadNative(binding.containerNative) {
//                actionShowAds(it)
//            }
        }
    }
}


interface ConversationHandleClick {
    fun itemClick(id: Long)
    fun itemLongClick(view: View, conversation: Conversation): Boolean
}