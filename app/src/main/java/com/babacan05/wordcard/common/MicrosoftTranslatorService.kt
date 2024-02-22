package com.babacan05.wordcard.common

import com.babacan05.wordcard.model.TranslationRequestBody
import com.babacan05.wordcard.model.TranslationResult
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MicrosoftTranslatorService {
    @POST("/translate?api-version=3.0&profanityAction=NoAction&textType=plain")
    suspend fun translateText(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Query("to") toLanguage: String,
        @Body requestBody: List<TranslationRequestBody>
    ): List<TranslationResult>
}
val okHttpClient = OkHttpClient.Builder()
    .readTimeout(6, TimeUnit.SECONDS) // Okuma zaman aşımı: 30 saniye
    .writeTimeout(6, TimeUnit.SECONDS) // Yazma zaman aşımı: 30 saniye
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("https://microsoft-translator-text.p.rapidapi.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

suspend fun getTranslate( toLanguage: String = "en", text: String ): String{
    val service = retrofit.create(MicrosoftTranslatorService::class.java)

// API isteğini gönderme ve yanıtı alıp işleme
    val requestBody = listOf(TranslationRequestBody(text))
    val apiKey = "0803bfd4b7msh9e9438d7bc813b1p13727fjsn6cc6106ad8b8"


    // Yanıtı işleme
    try {
        val response = service.translateText(apiKey, toLanguage, requestBody)
        // Yanıtı işleme
       var translationa=""
        response.forEach { result ->
            result.translations.forEach { translation ->
                println("Translated Text: ${translation.text}, Language: ${translation.to}")
                translationa=translation.text
            }
        }
       if(translationa==""){throw Exception()}
        else{return translationa}

    } catch (e: Exception) {
        e.printStackTrace()
        return  getGoogleTranslate(toLanguage,text)
    }
    }



