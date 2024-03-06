package com.babacan05.wordcard.common

import io.grpc.internal.TransportTracer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

data class Translator(
    val data: TranslatedData
)

data class TranslatedData(
    val translatedText: String
)
interface TranslatorService {
    @FormUrlEncoded
    @POST("translate")
  suspend  fun translateText(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Field("source_language") sourceLanguage: String,
        @Field("target_language") targetLanguage: String,
        @Field("text") text: String
    ):Translator
}

val retrofitTranslator = Retrofit.Builder()
    .baseUrl("https://text-translator2.p.rapidapi.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

val serviceTranslator = retrofitTranslator.create(TranslatorService::class.java)

suspend fun getTranslator(toLanguage:String,text:String):String{
    val apiKey = "***********************"
    val sourceLanguage = "auto"


    try {
        val call = serviceTranslator.translateText(apiKey, sourceLanguage, toLanguage, text).data.translatedText
        if(call==""){throw Exception()}else{return call}

    }catch (e:Exception){

return ""

    }
}










