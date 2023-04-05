package com.chatbotai.aichataiart.view.adapter

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chatbotai.aichataiart.databinding.ItemImageBinding
import com.chatbotai.aichataiart.network.model.generateImage.Message
import com.chatbotai.aichataiart.utils.CommonUtils.aesDecryptLinkImage
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class PhotoAdapter(private val actionDownload: ((String) -> Unit), private val actionZoom: ((String) -> Unit)) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    private val messenger = mutableListOf<Message>()
    private var isLoad = false

    init {
        messenger.add(Message(""))
        messenger.add(Message(""))
    }

    fun addData(mess: List<Message>) {
        messenger.clear()
        isLoad = true
        messenger.addAll(mess)
    }

    inner class ViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var glide: RequestManager

        init {
            val with = (Resources.getSystem().displayMetrics.widthPixels * 6 / 7)
            val params = binding.root.layoutParams
            params.width = with
            params.height = with
            binding.root.layoutParams = params
        }

        fun bin(messenger: Message) {
            binding.isLoadingImage = isLoad
            glide = Glide.with(binding.root)
            loadImage(aesDecryptLinkImage(messenger.url), binding.image) {
                binding.isLoadingImage = false
            }

            binding.btnDown.setOnClickListener {
                actionDownload(aesDecryptLinkImage(messenger.url))
            }

            binding.btnZoom.setOnClickListener {
                actionZoom(aesDecryptLinkImage(messenger.url))
            }
        }

        fun loadImage(res: String, v: View, action: (() -> Unit)? = null) {
            val handler = Handler()
            glide.load(res).centerInside().diskCacheStrategy(DiskCacheStrategy.ALL)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        handler.post {
                            loadImage(res, v, action)
                        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return messenger.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bin(messenger[position])
    }
}