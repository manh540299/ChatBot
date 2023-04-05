package com.chatbotai.aichataiart.view.activity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.database.model.ImageGenerated
import com.chatbotai.aichataiart.databinding.ActivityPhotoGeneratedBinding
import com.chatbotai.aichataiart.network.model.generateImage.Message
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.utils.PathUtil
import com.chatbotai.aichataiart.view.activity.base.BaseActivityWithDataBiding
import com.chatbotai.aichataiart.view.adapter.PhotoAdapter
import com.chatbotai.aichataiart.view.dialog.DialogZoomImage
import com.chatbotai.aichataiart.viewmodel.HistoryViewModel
import com.smarttech.smarttechlibrary.ads.AdsInterstitialManager
import com.smarttech.smarttechlibrary.utils.ManageSaveLocal
import kotlinx.coroutines.delay
import java.io.File

class PhotoGeneratedActivity : BaseActivityWithDataBiding<ActivityPhotoGeneratedBinding>(ActivityPhotoGeneratedBinding::inflate) {
    private val historyViewModel: HistoryViewModel by viewModels()
    private var isShareFile = false
    private val photoAdapter = PhotoAdapter(actionDownload = {
        downloadFile(url = it)
    }, actionZoom = {
        val dialog = DialogZoomImage(this@PhotoGeneratedActivity, it, { url ->
            shareFileFile(url)
        }, { url ->
            isShareFile = false
            downloadFile(url = url)
        })
        dialog.show()
    })
    private val onComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.extras?.let {
                Toast.makeText(this@PhotoGeneratedActivity, getString(R.string.download_complete), Toast.LENGTH_SHORT).show()
                val downloadedFileId = it.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)
                (getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager)?.let { downloadManager ->
                    val uri: Uri = downloadManager.getUriForDownloadedFile(downloadedFileId)
                    PathUtil.getPath(this@PhotoGeneratedActivity, uri)?.let { pathFile ->
                        if (isShareFile) {
                            CommonUtils.shareCollect(this@PhotoGeneratedActivity, listOf(pathFile))
                            return
                        }
                        receiveData(this@PhotoGeneratedActivity.intent) { _, message, style ->
                            Log.d("receiveData", "Download $message :: $style")
                            historyViewModel.insertHistory(ImageGenerated(path = pathFile, style = style ?: "", contentDraw = message ?: ""))
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(context: Context, list: List<Message>, message: String, style: String): Intent {
            return Intent(context, PhotoGeneratedActivity::class.java).apply {
                putParcelableArrayListExtra("list_message", list as ArrayList)
                putExtra("message", message)
                putExtra("style", style)
            }
        }

        private fun receiveData(intent: Intent, result: (ArrayList<Message>?, String?, String?) -> Unit) {
            if (Build.VERSION.SDK_INT >= 33)
                result(intent.getParcelableArrayListExtra("list_message", Message::class.java), intent.getStringExtra("message"), intent.getStringExtra("style"))
            else
                result(intent.getParcelableArrayListExtra("list_message"), intent.getStringExtra("message"), intent.getStringExtra("style"))
        }
    }

    override fun initData() {
        receiveData(intent) { listMessage, message, style ->
            Log.d("receiveData", "$message :: $style")
            listMessage?.let {
                binding.tvTitle.text = getString(R.string.photo_generated, it.size)
                photoAdapter.addData(it)
                binding.rlImage.adapter = photoAdapter
                binding.indicator.attachTo(binding.rlImage)
            }
        }

    }

    override fun initView() {
        lightStatusBar()
        marginStatusBar(listOf(binding.btnBack))
        binding.rlImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        (binding.rlImage.layoutParams as? ConstraintLayout.LayoutParams)?.let {
            it.topMargin = if (ManageSaveLocal.isPurchase()) CommonUtils.dpToPx(60) else CommonUtils.dpToPx(20)
            binding.rlImage.layoutParams = it
        }
    }

    override fun listenLiveData() {

    }

//    override fun onResume() {
//        super.onResume()
//        lifecycleScope.launchWhenResumed {
//            delay(200)
//            AdsInterstitialManager.loadNative(binding.container)
//        }
//    }

    override fun listeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun shareFileFile(url: String) {
        isShareFile = true
        downloadFile(url)
    }

    private fun downloadFile(url: String) {
        Toast.makeText(this@PhotoGeneratedActivity, getString(R.string.downloading_file), Toast.LENGTH_SHORT).show()
        val filename = "${getString(R.string.app_name)}_${System.currentTimeMillis()}.png"
        val direct = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/" + "OpenAI" + "/"
        )
        if (!direct.exists()) {
            direct.mkdir()
        }
        if (!CommonUtils.checkPermission(this))
            allowPermission {
                downloadFile(url)
            }
        else {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setTitle(filename)
            request.setMimeType("image/png")
            request.setDescription("Download Image...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                File.separator + "/OpenAI/" + filename
            )
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
    }

}