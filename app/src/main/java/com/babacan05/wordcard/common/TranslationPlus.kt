package com.babacan05.wordcard.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class TranslationPlus(
    val translations: Translations
)

data class Translations(
    val translation: String
)
data class TranslationRequest(
    val text: String,
    val source: String,
    val target: String
)
interface TranslatePlusService {
    @POST("translate")
   suspend fun translateText(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Body request: TranslationRequest
    ): TranslationPlus
}

val retrofitPlus = Retrofit.Builder()
    .baseUrl("https://translate-plus.p.rapidapi.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

val servicePlus = retrofitPlus.create(TranslatePlusService::class.java)

suspend fun getTranslatePlus(toLanguage:String,text:String):String{
    val apiKey = "df0e0b9ff7msh5e0b3c6e26ff767p1b6b6bjsn264a19ea3f64"

    val source = "auto"


    try {
        val request = TranslationRequest(text, source, toLanguage)
        val call = servicePlus.translateText(apiKey, request)
       val result=call.translations.translation
        if(result==""){throw Exception()}else{return result}

    }catch (e:Exception){

return getTranslator(toLanguage,text)


    }





}














