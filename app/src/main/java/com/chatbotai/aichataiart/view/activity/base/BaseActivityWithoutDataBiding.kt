package com.chatbotai.aichataiart.view.activity.base

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.view.activity.PaymentActivity
import com.chatbotai.aichataiart.view.activity.ShowPaymentFrom
import com.chatbotai.aichataiart.viewmodel.LoadAdsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.hariprasanths.bounceview.BounceView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.smarttech.smarttechlibrary.ads.AdsInterstitialManager
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T


abstract class BaseActivityWithoutDataBiding<Binding : ViewBinding>(private val inflate: Inflate<Binding>) : BaseActivity() {
    protected lateinit var binding: Binding

    override fun bindView() {
        binding = inflate(LayoutInflater.from(this), null, false)
        setContentView(binding.root)
    }
}


abstract class BaseActivityWithDataBiding<Binding : ViewDataBinding>(private val inflate: Inflate<Binding>) : BaseActivity() {
    protected lateinit var binding: Binding

    override fun bindView() {
        binding = inflate(LayoutInflater.from(this), null, false)
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }
}

abstract class BaseActivity : AppCompatActivity() {
    private val loadAdsViewModel by viewModels<LoadAdsViewModel>()
    private lateinit var glide: RequestManager
    private var mActivityResultPayment: ActivityResultLauncher<Intent>? = null

    private fun createActivityLauncher() {
        mActivityResultPayment = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(javaClass.simpleName, "resultCode=${it.resultCode}")
            when (it.resultCode) {
                PaymentActivity.PAYMENT_SUCCESS -> {
                    purchaseSuccess()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        with(window) {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (isIgnoreTransparent()) {
                statusBarColor = Color.WHITE
                navigationBarColor = Color.WHITE
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            } else {
                setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
            }
        }
        super.onCreate(savedInstanceState)
        createActivityLauncher()
        bindView()
        glide = Glide.with(this)
        initView()
        listenLiveData()
        listeners()
        initData()

        loadAdsViewModel.timeLoadAds.observe(this) {
            lifecycleScope.launch(Dispatchers.Main) {
                AdsInterstitialManager.loadAds(this@BaseActivity)
            }
        }
    }


    open fun isIgnoreTransparent(): Boolean {
        return false
    }

    abstract fun bindView()
    abstract fun initData()
    abstract fun initView()
    abstract fun listenLiveData()
    abstract fun listeners()
    open fun purchaseSuccess() {}
    open fun onSaveState(outState: Bundle) {}
    open fun onRestoreState(savedInstanceState: Bundle) {}


    protected fun allowPermission(action: () -> Unit) {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                Log.d(
                    "onPermissionsChecked=",
                    "onPermissionsChecked ${p0?.deniedPermissionResponses?.size ?: -1};${p0?.grantedPermissionResponses?.size ?: -1}; ${p0?.isAnyPermissionPermanentlyDenied ?: false}"
                )
                if ((p0?.grantedPermissionResponses?.size ?: 0) == 1) {
                    action()
                } else {
                    if (p0?.isAnyPermissionPermanentlyDenied == true) {
                        com.smarttech.smarttechlibrary.utils.CommonUtils.gotoSetting(this@BaseActivity)
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                Log.d("onPermissionsChecked=", "onPermissionRationaleShouldBeShown")
                p1?.continuePermissionRequest()
            }

        }).check()
    }


    protected fun lightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black_main)
        }
    }

    protected fun darkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black_main)
        }
    }

    open fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        result = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            200
        }
        return result
    }

    open fun getNavigationBarHeight(): Int {
        var result = CommonUtils.dpToPx(40)
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val hasNavBarId = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        if ((ViewConfiguration.get(this).hasPermanentMenuKey() || (hasNavBarId > 0 && resources.getBoolean(hasNavBarId)))) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    open fun marginStatusBar(listView: List<View>) {
        for (i in listView) {
            val params = i.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = params.topMargin + getStatusBarHeight()
            i.layoutParams = params
        }
    }

    open fun marginNavigationBar(listView: List<View>) {
        for (i in listView) {
            val params = i.layoutParams as ConstraintLayout.LayoutParams
            params.bottomMargin = params.bottomMargin + getNavigationBarHeight()
            i.layoutParams = params
        }
    }

    open fun paddingNavigationBar(listView: List<View>) {
        for (i in listView) {
            i.setPadding(i.paddingLeft, i.paddingTop, i.paddingRight, getNavigationBarHeight())
        }
    }

    open fun paddingRootView(rootViewActivity: View) {
        rootViewActivity.setPadding(
            rootViewActivity.paddingLeft,
            getStatusBarHeight(),
            rootViewActivity.paddingRight,
            rootViewActivity.paddingBottom
        )
    }

    fun showPayment(showPaymentFrom: ShowPaymentFrom = ShowPaymentFrom.INTRO) {
        try {
            Intent(this, PaymentActivity::class.java).apply {
                putExtra("ShowPaymentFrom", showPaymentFrom)
                mActivityResultPayment?.launch(this)
            }
        } catch (_: ActivityNotFoundException) {
        }
    }

    protected fun showAds(action: () -> Unit) {
        AdsInterstitialManager.showAds(this, action)
    }

    protected fun loadImage(res: Int, v: View, action: (() -> Unit)? = null) {
        glide.load(res).centerInside().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
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

    protected fun loadImage(res: String, v: View, action: (() -> Unit)? = null) {
        glide.load(res).centerInside().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .addListener(object : RequestListener<Drawable> {
                val handler = Handler()
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    protected fun loadImage(res: Int, v: View) {
        glide.load(res).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(v as ImageView)
    }

    protected fun loadImage(res: String?, v: View) {
        glide.load(res).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(v as ImageView)
    }

    protected fun goToNewActivity(activity: Class<*>) {
        startActivity(Intent(this, activity))
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        onRestoreState(savedInstanceState)
    }

    override fun onTrimMemory(level: Int) {
        Log.d("BaseActivity", "onTrimMemory")
        glide.onTrimMemory(TRIM_MEMORY_MODERATE)
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("BaseActivity", "onLowMemory")
    }

    fun addBounceView(listView: List<View>) {
        listView.forEach {
            BounceView.addAnimTo(it).setScaleForPushInAnim(1.02f, 1.02f).setScaleForPopOutAnim(1.02f, 1.02f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivityResultPayment = null
    }
}
