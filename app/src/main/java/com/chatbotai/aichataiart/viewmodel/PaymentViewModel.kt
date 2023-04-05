package com.chatbotai.aichataiart.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import com.chatbotai.aichataiart.viewmodel.base.BaseViewModel
import com.chatbotai.aichataiart.view.activity.PaymentActivity.Companion.IAP_YEARLY
import com.chatbotai.aichataiart.view.activity.PaymentActivity.Companion.IAP_WEEKLY
import com.smarttech.smarttechlibrary.bus.PaymentDetail

class PaymentViewModel(application: Application, savedStateHandle: SavedStateHandle) : BaseViewModel(application, savedStateHandle) {
    val isPayingIap = ObservableField(false)
    val isSelectedYearly = ObservableField(true)
    val isPriceOne = ObservableField(PaymentDetail(IAP_YEARLY, "Yearly"))
    val isPriceTwo = ObservableField(PaymentDetail(IAP_WEEKLY, "Weekly"))

    fun insertPaymentDetail(listPaymentDetail: List<PaymentDetail>) {
        for (i in listPaymentDetail) {
            if (i.id == IAP_YEARLY) {
                isPriceOne.set(i)
            } else if (i.id == IAP_WEEKLY) {
                isPriceTwo.set(i)
            }
        }
    }

    fun setStatePayingIap(isPayingIap: Boolean) {
        this.isPayingIap.set(isPayingIap)
    }
}