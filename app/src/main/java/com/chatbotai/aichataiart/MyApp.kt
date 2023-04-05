package com.chatbotai.aichataiart

import android.app.Application
import com.chatbotai.aichataiart.utils.LogEventUtils
import com.chatbotai.aichataiart.utils.ManageSaveLocal
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.orhanobut.hawk.Hawk
import com.smarttech.smarttechlibrary.utils.SmartTechInit
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
        ViewPump.init(
            ViewPump.builder().addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Inter-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
                )
            ).build()
        )
        SmartTechInit.init(this)
        if (BuildConfig.DEBUG) {
//            SmartTechInit.setTestDevices(isTestBilling = false, ignoreInstallFromStore = false)
            com.smarttech.smarttechlibrary.utils.ManageSaveLocal.setIsPurchase(false)
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            ManageSaveLocal.setIsFirstOpenApp(true)
            ManageSaveLocal.setResetAccessSendMessage()
        } else {
            LogEventUtils.createInstance(this@MyApp)
        }
        SmartTechInit.setTestDevices(ignoreInstallFromStore = true, isTestBilling = true)
    }
}