package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.DialogQuestionClearConversationBinding
import com.chatbotai.aichataiart.view.dialog.base.BaseDialogDataBinding

class DialogQuestionClearConversation(context: Context, private val typeCleanConversation: TypeCleanConversation = TypeCleanConversation.CONVERSATION, private val action: () -> Unit) :
    BaseDialogDataBinding<DialogQuestionClearConversationBinding>(context, DialogQuestionClearConversationBinding::inflate) {
    override fun initView() {
        super.initView()
        binding.typeCleanConversation = typeCleanConversation
    }

    override fun listener() {
        super.listener()
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            dismiss()
            action()
        }
    }
}

enum class TypeCleanConversation {
    CONVERSATION, HISTORY, MESSAGE, ONLY_CONVERSATION, DELETE_IMAGE
}


@BindingAdapter("title_dialog_question_action")
fun setTitleDialogQuestionAction(view: TextView, typeCleanConversation: TypeCleanConversation) {
    when (typeCleanConversation) {
        TypeCleanConversation.MESSAGE -> {
            view.setText(R.string.clear_history)
        }
        TypeCleanConversation.CONVERSATION -> {
            view.setText(R.string.clear_all)
        }
        TypeCleanConversation.HISTORY -> {
            view.setText(R.string.clear_history)
        }
        TypeCleanConversation.ONLY_CONVERSATION -> {
            view.setText(R.string.clear_history)
        }
        TypeCleanConversation.DELETE_IMAGE -> {
            view.setText(R.string.delete_image)
        }
    }
}

@BindingAdapter("content_clear_conversation")
fun setContentClear(view: TextView, typeCleanConversation: TypeCleanConversation) {
    when (typeCleanConversation) {
        TypeCleanConversation.MESSAGE -> {
            view.setText(R.string.content_clear_message)
        }
        TypeCleanConversation.CONVERSATION -> {
            view.setText(R.string.content_clear_conversation)
        }
        TypeCleanConversation.HISTORY -> {
            view.setText(R.string.content_clear_history)
        }
        TypeCleanConversation.ONLY_CONVERSATION -> {
            view.setText(R.string.content_clear_only_conversation)
        }
        TypeCleanConversation.DELETE_IMAGE -> {
            view.setText(R.string.content_delete_image)
        }
    }
}