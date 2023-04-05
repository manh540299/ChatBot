package com.chatbotai.aichataiart.view.activity

import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.Keep
import com.chatbotai.aichataiart.R
import com.chatbotai.aichataiart.databinding.ActivityPaymentBinding
import com.chatbotai.aichataiart.utils.CommonUtils
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.UserFeature
import com.chatbotai.aichataiart.view.activity.base.BaseActivityIap
import com.chatbotai.aichataiart.viewmodel.PaymentViewModel
import com.smarttech.smarttechlibrary.billing.BillingManager
import com.smarttech.smarttechlibrary.billing.StatePurchase

class PaymentActivity : BaseActivityIap<ActivityPaymentBinding>(ActivityPaymentBinding::inflate) {
    private val paymentViewModel: PaymentViewModel by viewModels()
    private lateinit var showPaymentFrom: ShowPaymentFrom

    companion object {
        const val IAP_YEARLY = "sub_year"
        const val IAP_WEEKLY = "sub_week"
        const val PAYMENT_SUCCESS = 304985
    }

    override fun initData() {
        lightStatusBar()
        showPaymentFrom = (intent.getSerializableExtra("ShowPaymentFrom") as? ShowPaymentFrom) ?: ShowPaymentFrom.INTRO
        Log.d("showPaymentFrom", "$showPaymentFrom")
        BillingManager.getInstance(this).listPaymentDetail.observe(this) {
            paymentViewModel.insertPaymentDetail(it)
        }
        startAnimation(
            1.03f,
            1f,
            binding.btnUpgradeNow,
            binding.viewAlphaHover,
            binding.viewAlphaHover1,
            binding.viewAlphaHover2,
            binding.viewAlphaHover3
        )

        LogEventUtils.logFeature(UserFeature.IAP)
    }

    override fun initView() {
        marginStatusBar(listOf(binding.btnClose))
        binding.paymentViewModel = paymentViewModel
        loadImage(R.drawable.ic_clear_text, binding.btnClose)
        binding.rootView.setPadding(0, 0, 0, binding.rootView.paddingBottom + getNavigationBarHeight())
    }

    override fun listenLiveData() {
        BillingManager.getInstance(this).eventPurchase.observe(this) {
            Log.d("BillingManager", "$it")
            paymentViewModel.setStatePayingIap(false)
            when (it.statePurchase) {
                StatePurchase.Success -> {
                    Toast.makeText(this, getString(R.string.purchase_success), Toast.LENGTH_SHORT).show()
                    paymentSuccess(it.idProduct)
                }
                StatePurchase.Error -> {
                    Toast.makeText(this, getString(R.string.purchase_error), Toast.LENGTH_SHORT).show()
                }
                StatePurchase.Cancel -> {

                }
                StatePurchase.Restore -> {
                    Toast.makeText(this, getString(R.string.restore_purchases_success), Toast.LENGTH_SHORT).show()
                    paymentSuccess(it.idProduct, true)
                }
                StatePurchase.NOT_INSTALL_FROM_STORE -> {
                    Toast.makeText(this, getString(R.string.install_app_from_store), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun listeners() {
        binding.btnSelectYear.setOnClickListener {
            paymentViewModel.isSelectedYearly.set(true)
        }

        binding.btnSelectWeek.setOnClickListener {
            paymentViewModel.isSelectedYearly.set(false)
        }

        binding.btnUpgradeNow.setOnClickListener {
            if (paymentViewModel.isPayingIap.get() == true) {
                return@setOnClickListener
            }
            paymentViewModel.setStatePayingIap(true)
            if (paymentViewModel.isSelectedYearly.get() == true) {
                BillingManager.getInstance(this).buySubscription(IAP_YEARLY, this)
            } else {
                BillingManager.getInstance(this).buySubscription(IAP_WEEKLY, this)
            }
        }

        binding.btnPolicy.setOnClickListener {
            CommonUtils.gotoPrivacyPolicy(this)
        }

        binding.btnTermsOfUse.setOnClickListener {
            CommonUtils.gotoTermsOfUse(this)
        }

        binding.btnClose.setOnClickListener {
            closePayment()
        }

        onBackPressedDispatcher.addCallback {
            closePayment()
        }
    }

    private fun closePayment() {
        if (showPaymentFrom == ShowPaymentFrom.INTRO || showPaymentFrom == ShowPaymentFrom.ON_BOARDING) {
            showAds {
                goToNewActivity(MainActivity::class.java)
                finish()
            }
        } else {
            finish()
        }
    }

    private fun paymentSuccess(productId: String, isRestore: Boolean = false) {
        if (!isRestore) {
            LogEventUtils.logIAP(showPaymentFrom, productId)
        }
        if (showPaymentFrom == ShowPaymentFrom.INTRO || showPaymentFrom == ShowPaymentFrom.ON_BOARDING) {
            goToNewActivity(MainActivity::class.java)
        } else {
            setResult(PAYMENT_SUCCESS)
        }
    }

    override fun onResume() {
        super.onResume()
        paymentViewModel.isPayingIap.set(false)
    }
}


@Keep
enum class ShowPaymentFrom {
    INTRO, ON_BOARDING, MAIN, SETTING, QUESTION, DETAIL_CONVERSATION, GENERATE_IMAGE
}
