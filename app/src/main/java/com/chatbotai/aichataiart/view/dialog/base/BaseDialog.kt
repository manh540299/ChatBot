package com.chatbotai.aichataiart.view.dialog.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.view.activity.base.Inflate

abstract class BaseDialog<Binding : ViewBinding>(
    context: Context,
    private val inflate: Inflate<Binding>,
    private val isTransparentBackground: Boolean = false,
    @StyleRes themeResId: Int = R.style.CustomMaterialDialog
) : Dialog(context, themeResId) {
    protected lateinit var binding: Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(LayoutInflater.from(context), null, false)
        if (isTransparentBackground) {
            window?.decorView?.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }
        setWidth()
        setContentView(binding.root)
        initData()
        initView()
        listener()
    }

    open fun setWidth() {}

    open fun initData() {}
    open fun initView() {}
    open fun listener() {}

    protected fun setupBottomDialog() {
        window?.attributes?.windowAnimations = R.style.DialogAnim
        window?.setGravity(Gravity.BOTTOM)
    }

}


abstract class BaseDialogDataBinding<Binding : ViewDataBinding>(
    context: Context,
    private val inflate: Inflate<Binding>,
    @StyleRes themeResId: Int = R.style.CustomMaterialDialog
) : Dialog(context, themeResId) {
    protected lateinit var binding: Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(LayoutInflater.from(context), null, false)
        setContentView(binding.root)
        initData()
        initView()
        listener()
    }

    open fun initData() {}
    open fun initView() {}
    open fun listener() {}

    protected fun setupBottomDialog() {
        window?.attributes?.windowAnimations = R.style.DialogAnim
        window?.setGravity(Gravity.BOTTOM)
    }

}