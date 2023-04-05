package com.chatbotai.aichataiart.view.adapter

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.database.model.ContentConversation
import com.chatbotai.aichataiart.databinding.ItemBotContentConversationBinding
import com.chatbotai.aichataiart.databinding.ItemMyContentConversationBinding
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.utils.Constants
import com.chatbotai.aichataiart.utils.TimeUtils
import com.chatbotai.aichataiart.view.custom.PopupMenuCustomLayout

class ContentConversationAdapter(private val action: (Any) -> Unit) : PagingDataAdapter<ContentConversation, RecyclerView.ViewHolder>(DiffContentConversation()) {
    companion object {
        var isFirstLoadChat = false
    }

    class DiffContentConversation : DiffUtil.ItemCallback<ContentConversation>() {
        override fun areContentsTheSame(oldItem: ContentConversation, newItem: ContentConversation): Boolean {
            if (newItem.message.isEmpty()) {
                return false
            }
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: ContentConversation, newItem: ContentConversation): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun getItemViewType(position: Int): Int {
        peek(position)?.let {
            if (position + 1 == itemCount) {
                return if (it.isBot) {
                    0 // Have Time, Is Bot
                } else {
                    2 // Have Time, No Bot
                }
            } else {
                peek(position + 1)?.let { oldItem ->
                    if (checkViewTypeHaveTime(it, oldItem)) {
                        return if (it.isBot) {
                            0 // Have Time, Is Bot
                        } else {
                            2 // Have Time, No Bot
                        }
                    } else {
                        return if (it.isBot) {
                            1 // No Time, Is Bot
                        } else {
                            3 // Not Time, No Bot
                        }
                    }
                }

            }
        }
        return 0
    }

    private fun checkViewTypeHaveTime(newItem: ContentConversation, oldItem: ContentConversation): Boolean {
        return newItem.time - oldItem.time > 60000
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType <= 1) {
            ViewBotContentConversationHolder(
                ItemBotContentConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false), action
            )
        } else {
            ViewMyContentConversationHolder(
                ItemMyContentConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false), action
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder) {
                is ViewMyContentConversationHolder -> {
                    holder.bind(it, viewType = getItemViewType(position))
                }
                is ViewBotContentConversationHolder -> {
                    holder.bind(it, viewType = getItemViewType(position), position == 0)
                }
            }
        }
    }

    class ViewMyContentConversationHolder(private val binding: ItemMyContentConversationBinding, action: (Any) -> Unit) : BaseContentConversationViewHolder(binding.root, action) {

        init {
            val param = binding.txtMessage.layoutParams as ConstraintLayout.LayoutParams
            param.matchConstraintMaxWidth = (Resources.getSystem().displayMetrics.widthPixels / 1.1f).toInt()
            binding.txtMessage.layoutParams = param
            binding.contentConversationHandleClick = this
        }

        fun bind(contentConversation: ContentConversation, viewType: Int) {
            Log.d("viewType=", "$viewType")
            if (viewType == 2) {
                binding.time = TimeUtils.getTimeFormatApp(contentConversation.time)
            } else {
                binding.time = ""
            }
            binding.contentConversation = contentConversation
        }
    }

    class ViewBotContentConversationHolder(private val binding: ItemBotContentConversationBinding, action: (Any) -> Unit) : BaseContentConversationViewHolder(binding.root, action) {
        init {
            val param = binding.txtMessage.layoutParams as ConstraintLayout.LayoutParams
            param.matchConstraintMaxWidth = (Resources.getSystem().displayMetrics.widthPixels / 1.2f).toInt()
            binding.txtMessage.layoutParams = param
            binding.contentConversationHandleClick = this
            binding.txtMessage.setCharacterDelay(56)
            binding.txtMessage.avoidTextOverflowAtEdge(false)
            binding.txtMessage.setOnAnimationChangeListener {
                isFirstLoadChat = false
            }
        }

        fun bind(contentConversation: ContentConversation, viewType: Int, isFirstItem: Boolean = false) {
            if (viewType == 0) {
                binding.time = TimeUtils.getTimeFormatApp(contentConversation.time)
            } else {
                binding.time = ""
            }

            binding.contentConversation = contentConversation
            if (isFirstItem) {
                if (isFirstLoadChat) {
                    binding.txtMessage.animateText(contentConversation.message)
                } else {
                    setMessageNormal(contentConversation.message)
                }
            } else {
                setMessageNormal(contentConversation.message)
            }
        }

        private fun setMessageNormal(message: String) {
            binding.txtMessage.stopAnimation()
            if (message.isEmpty()) {
                binding.animationTyping.playAnimation()
                binding.txtMessage.text = Constants.GPT_TYPING
            } else {
                binding.animationTyping.pauseAnimation()
                binding.txtMessage.text = message
            }
        }
    }
}

abstract class BaseContentConversationViewHolder(view: View, private val action: (Any) -> Unit) : RecyclerView.ViewHolder(view), ContentConversationHandleClick {
    override fun itemClick(view: View, contentConversation: ContentConversation) {
        PopupMenuCustomLayout(view.context, R.layout.layout_menu_detail_conversation) { menuItemId ->
            when (menuItemId) {
                R.id.btnCopy -> {
                    CommonUtils.copyClipboard(itemView.context, contentConversation.message)
                }
                R.id.btnDelete -> {
                    action(contentConversation.id)
                }
            }
        }.show(view)
    }
}

interface ContentConversationHandleClick {
    fun itemClick(view: View, contentConversation: ContentConversation)
}