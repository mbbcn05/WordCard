package com.babacan05.wordcard.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

data class TranslationResponse(
    val trans: String
)


interface TranslateService {
    @POST("api/v1/translator/text")
    @FormUrlEncoded
   suspend fun translateText(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Field("from") from: String,
        @Field("to") to: String,
        @Field("text") text: String
    ):TranslationResponse
}





val retrofitGoogleTranlate = Retrofit.Builder()
    .baseUrl("https://google-translate113.p.rapidapi.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)

    .build()

val serviceGoogleTranslate = retrofitGoogleTranlate.create(TranslateService::class.java)

suspend fun getGoogleTranslate(toLanguage:String,text:String):String{
    val apiKey = "df0e0b9ff7msh5e0b3c6e26ff767p1b6b6bjsn264a19ea3f64"
    val from = "auto"




    try {
        val result= serviceGoogleTranslate.translateText(apiKey, from, toLanguage, text).trans
        if(result==""){throw Exception()}else{return result}
    } catch (e: Exception) {
        return getTranslatePlus(toLanguage,text)
    }



}




