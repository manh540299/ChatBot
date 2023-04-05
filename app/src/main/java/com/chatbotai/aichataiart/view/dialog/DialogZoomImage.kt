package com.chatbotai.aichataiart.view.dialog

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.chatbotai.aichataiart.databinding.DialogImgeGenerateBinding
import com.chatbotai.aichataiart.view.dialog.base.BaseDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class DialogZoomImage(context: Context, private val url: String, private val actionShare: (String) -> Unit, private val actionDownLoad: (String) -> Unit) :
    BaseDialog<DialogImgeGenerateBinding>(context, DialogImgeGenerateBinding::inflate, true) {
    private lateinit var glide: RequestManager

    override fun setWidth() {
        super.setWidth()
        val displayRectangle = Rect()
        window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
        binding.root.minWidth = (displayRectangle.width()) // width
        binding.root.minHeight = (displayRectangle.height() * 0.8f).toInt() // Height
        binding.img.maxHeight = (displayRectangle.height() * 0.7f).toInt()
    }

    override fun listener() {
        super.listener()
        binding.btnDismiss.setOnClickListener {
            dismiss()
        }
        glide = Glide.with(binding.root)
        loadImage(url, binding.img)


        binding.btnShare.setOnClickListener {
            actionShare(url)
        }

        binding.btnDownload.setOnClickListener {
            actionDownLoad(url)
        }
    }

    fun loadImage(res: String, v: View, action: (() -> Unit)? = null) {
        val handler = Handler()
        glide.load(res).centerInside().diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    handler.postDelayed({
                        loadImage(res, v, action)
                    }, 2000)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    action?.let {
                        it()
                    }
                    return false
                }
            }).into(v as ImageView)
    }
}