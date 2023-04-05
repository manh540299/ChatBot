package com.chatbotai.aichataiart.network.retrofit

import com.chatbotai.aichataiart.network.model.generateImage.ResultGenerateImage
import com.chatbotai.aichataiart.network.model.question_ai.ResultQuestion
import io.reactivex.Single
import retrofit2.http.*

interface APIInterface {

    @POST("images/generations")
    @Headers("accept: application/json", "Authorization: Bearer 4|aKmWGIzuY5oU94Ib0adbjAwCvNc34PN3cQcbYEte")
    @FormUrlEncoded
    fun sendGenerateImage(@Field("prompt") content: String): Single<ResultGenerateImage>


    @POST("chat/completions")
    @Headers("accept: application/json", "Authorization: Bearer 4|aKmWGIzuY5oU94Ib0adbjAwCvNc34PN3cQcbYEte")
    @FormUrlEncoded
    fun sendQuestion(@Field("prompt") message: String): Single<ResultQuestion>
}