package com.chatbotai.aichataiart.view.fragment.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.chatbotai.aichataiart.utils.AnimUtils
import com.chatbotai.aichataiart.utils.CommonUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<Binding : ViewDataBinding>(private val inflate: Inflate<Binding>) : Fragment() {
    private lateinit var glide: RequestManager
    protected lateinit var binding: Binding
    private var mActivityResult: ActivityResultLauncher<Intent>? = null

    private fun createActivityResultLauncher() {
        mActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, container, false)
        binding.lifecycleOwner = this
//        createActivityResultLauncher()
        onCreate()
        glide = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        resultStateViewWithPurchase(if (ManageSaveLocal.isPurchase()) View.GONE else View.VISIBLE)
        initData()
        initView()
        listener()
        listenLiveData()
    }

    open fun onCreate() {}
    protected abstract fun initData()
    protected abstract fun initView()
    protected abstract fun listenLiveData()
    protected abstract fun listener()
    open fun resultStateViewWithPurchase(visibility: Int) {}

    fun goToNewActivity(activity: Class<*>) {
        startActivity(Intent(requireActivity(), activity))
    }

    protected fun loadImage(res: Int, v: View) {
        glide.load(res).diskCacheStrategy(DiskCacheStrategy.NONE).centerInside().into(v as ImageView)
    }

    protected fun loadImage(res: String?, v: View) {
        glide.load(res).diskCacheStrategy(DiskCacheStrategy.NONE).centerInside().into(v as ImageView)
    }

    open fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


    open fun getNavigationBarHeight(): Int {
        var result = CommonUtils.dpToPx(36)
        val resourceId = requireActivity().resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val hasNavBarId = requireActivity().resources.getIdentifier("config_showNavigationBar", "bool", "android")
        if ((ViewConfiguration.get(requireContext()).hasPermanentMenuKey() || (hasNavBarId > 0 && resources.getBoolean(hasNavBarId)))) {
            result = requireActivity().resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun marginStatusBar(view: View) {
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        params.height = getStatusBarHeight()
        view.layoutParams = params
    }

    fun marginViewWithStatusBar(view: View) {
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = params.topMargin + getStatusBarHeight()
        view.layoutParams = params
    }

    fun marginViewWithStatusBar(list: List<View>) {
        for (view in list) {
            val params = view.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = params.topMargin + getStatusBarHeight()
            view.layoutParams = params
        }
    }

    fun paddingViewWithNavigationBar(view: View) {
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, getNavigationBarHeight() + view.paddingBottom)
    }

    fun marginViewWithNavigationBar(view: View) {
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        params.bottomMargin = params.bottomMargin + getNavigationBarHeight()
        view.layoutParams = params
    }

    fun marginViewWithNavigationBar(list: List<View>) {
        for (view in list) {
            val params = view.layoutParams as ConstraintLayout.LayoutParams
            params.bottomMargin = params.bottomMargin + getNavigationBarHeight()
            view.layoutParams = params
        }
    }

    fun boundView(listView: List<View>) {
        listView.map {
            AnimUtils.boundView(it)

        }
    }

    protected fun addAnimButton(list: List<View>) {
        list.map {
            AnimUtils.boundView(it)
        }
    }

    fun setAnimation(idAnim: Int, idView: View) {
        val animation: Animation = AnimationUtils.loadAnimation(requireContext(), idAnim)
        idView.animation = animation
    }

    override fun onResume() {
//        resultStateViewWithPurchase(if (ManageSaveLocal.isPurchase()) View.GONE else View.VISIBLE)
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivityResult = null
    }
}