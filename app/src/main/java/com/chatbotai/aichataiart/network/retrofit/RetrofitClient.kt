package com.chatbotai.aichataiart.network.retrofit

import com.chatbotai.aichataiart.utils.Constants.TIMEOUT
import com.chatbotai.aichataiart.utils.Constants.createBuilderDemo
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val BASE_URL_APP = "https://betagpt.betasoftvn.com/api/"

object RetrofitClient {

    fun getClientApp(): APIInterface {
//        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(BASE_URL_APP)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIInterface::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getRequestInterceptor())
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .callTimeout(TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun getRequestInterceptor(): Interceptor {
        return Interceptor { chain ->
            chain.proceed(
                createBuilderDemo(
                    chain.request()
                        .newBuilder()
                ).build()
            )
        }
    }
}