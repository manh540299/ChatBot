package com.chatbotai.aichataiart.view.bindview

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.bus.NetworkState
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.view.activity.StateSelect
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


@BindingAdapter("image_resource")
fun setImageResource(view: ImageView, res: Int) {
    Glide.with(view).load(res).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(view)
}

@BindingAdapter("image_from_path")
fun setImageFromPath(view: ImageView, res: String) {
    Glide.with(view).load(res).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(view)
}

@BindingAdapter("background_resource")
fun loadBackgroundResource(view: View, res: Int) {
    view.setBackgroundResource(res)
}

@BindingAdapter("tint_image")
fun setTintColor(view: ImageView, res: Int) {
    view.setColorFilter(res)
}

@BindingAdapter("text_resource")
fun loadTextResource(view: TextView, res: Int) {
    view.setText(res)
}

@BindingAdapter("content_state_generate")
fun setContentStateGenerate(view: TextView, networkState: NetworkState?) {
    when (networkState?.status ?: NetworkState.State.LOADING) {
        NetworkState.State.FAILED -> {
            view.setText(if (CommonUtils.isNetworkConnected(view.context)) R.string.error_generate_image else R.string.check_internet)
        }
        NetworkState.State.LOADED -> {
            view.setText(R.string.generate_image_success)
        }
        NetworkState.State.LOADING -> {
            view.setText(R.string.you_can_stop_creating_here)
        }
    }
}

@BindingAdapter("background_generate_image")
fun setBackgroundImageProcessing(view: View, networkState: NetworkState?) {
    when (networkState?.status ?: NetworkState.State.LOADING) {
        NetworkState.State.FAILED -> {
            view.setBackgroundResource(R.drawable.bg_generate_image_error)
        }
        NetworkState.State.LOADED -> {

        }
        NetworkState.State.LOADING -> {
            view.setBackgroundResource(R.drawable.bg_generating_image)
        }
    }
}

@BindingAdapter("animation_generate_image")
fun setAnimationImageProcessing(view: LottieAnimationView, networkState: NetworkState?) {
    when (networkState?.status ?: NetworkState.State.LOADING) {
        NetworkState.State.FAILED, NetworkState.State.LOADED -> {
            view.visibility = View.GONE
            view.pauseAnimation()
        }
        NetworkState.State.LOADING -> {
            view.visibility = View.VISIBLE
            view.playAnimation()
        }
    }
}

@BindingAdapter("state_button_stop_generate_image")
fun setTextColorStopImageProcessing(view: TextView, networkState: NetworkState?) {
    when (networkState?.status ?: NetworkState.State.LOADING) {
        NetworkState.State.FAILED -> {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.white))
            view.setBackgroundResource(R.drawable.ripple_button_generate_image_error)
            view.setText(R.string.retry)
        }
        NetworkState.State.LOADED -> {
            view.visibility = View.GONE
        }
        NetworkState.State.LOADING -> {
            view.setTextColor(ContextCompat.getColor(view.context, R.color.black_main))
            view.setBackgroundResource(R.drawable.ripple_button_stop)
            view.setText(R.string.stop)
        }
    }
}

@BindingAdapter("image_from_drawable")
fun setImageFromDrawable(view: ImageView, res: Drawable) {
    view.setImageDrawable(res)
}


@BindingAdapter("content_state_select", "is_select_all")
fun setContentStateSelect(view: TextView, stateSelect: StateSelect?, isSelectAll: Boolean) {
    if (isSelectAll) {
        view.setText(R.string.deselect_all)
    } else {
        stateSelect?.let {
            when (stateSelect) {
                StateSelect.Select -> {
                    view.setText(R.string.select)
                }
                StateSelect.SelectAll -> {
                    view.setText(R.string.select_all)
                }
                StateSelect.DeselectAll -> {
                    view.setText(R.string.deselect_all)
                }
            }
        }
    }
}

@BindingAdapter("state_checked_history")
fun setStateCheckedHistory(view: ImageView, stateChecked: Int) {
    when (stateChecked) {
        0 -> {
            view.visibility = View.INVISIBLE
        }
        1 -> {
            view.setImageResource(R.drawable.ic_deselect_history)
            view.visibility = View.VISIBLE
        }
        2 -> {
            view.setImageResource(R.drawable.ic_selected_history)
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("content_component_state_history", "number_item_select_history")
fun setContentComponentStateHistory(view: TextView, stateChecked: StateSelect, numberItemHistorySelect: Int) {
    when (stateChecked) {
        StateSelect.Select -> {
            view.setText(R.string.history)
        }
        else -> {
            view.text = view.context.getString(R.string.history_selected, numberItemHistorySelect)
        }
    }
}

@BindingAdapter("number_remaining")
fun setNumberRemaining(v : TextView, numberRemaining : Int?){
    v.text = v.context.getString(R.string.remaining_messages, numberRemaining ?: 0)
}

