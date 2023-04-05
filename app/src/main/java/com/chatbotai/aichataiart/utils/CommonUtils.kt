package com.chatbotai.aichataiart.utils

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.chatbotai.aichataiart.R
import com.libbtech.antivirus.security.ChCrypto
import com.smarttech.smarttechlibrary.utils.CommonUtils
import java.io.File


object CommonUtils {

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun checkPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return true
        }
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val res = context.checkCallingOrSelfPermission(permission)
        return (res == PackageManager.PERMISSION_GRANTED)
    }

    fun copyClipboard(context: Context, text: String) {
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = if (text.isEmpty()) {
            ClipData.newPlainText("Copied Content", null)
        } else {
            ClipData.newPlainText("Copied Content", text)
        }
        clipboard.setPrimaryClip(clip)
    }

    fun sharePalette(bitmap: Bitmap, context: Context) {
        val bitmapPath = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, context.getString(R.string.app_name), "Share Conversation")
        val bitmapUri: Uri = Uri.parse(bitmapPath)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        context.startActivity(Intent.createChooser(intent, "Share"))
    }

    fun gotoPrivacyPolicy(context: Context) {
        CommonUtils.goToLink(context, "https://sites.google.com/view/privacy-policy-betasoft-inc?pli=1")
    }

    fun gotoTermsOfUse(context: Context) {
        CommonUtils.goToLink(context, "https://sites.google.com/view/privacy-policy-betasoft-inc?pli=1")
    }

    fun shareCollect(context: Context, listPath: List<String>) {
        try {
            val listUri = ArrayList<Uri>()
            for (i in listPath) {
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    File(i)
                )
                listUri.add(contentUri)
            }
            val i = ShareCompat.IntentBuilder.from(CommonUtils.getActivity(context)!!)
                .setType("*/*")
                .intent.setAction(Intent.ACTION_SEND_MULTIPLE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUri)

            context.startActivity(
                Intent.createChooser(
                    i,
                    context.getString(R.string.share_to_friend)
                )
            )
        } catch (ex: IllegalArgumentException) {
            Toast.makeText(
                context,
                context.getString(R.string.cant_share_file),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun aesDecryptLinkImage(url: String): String {
        return try {
            ChCrypto.aesDecrypt(url)
        } catch (ex: IllegalArgumentException) {
            url
        }
    }


    fun isNetworkConnected(context: Context): Boolean {
        val cm: ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        var isHaveInternet = false
        if (cm != null) {
            isHaveInternet = cm.activeNetworkInfo != null && cm.activeNetworkInfo?.isConnected ?: false
        }
        return isHaveInternet
    }
}