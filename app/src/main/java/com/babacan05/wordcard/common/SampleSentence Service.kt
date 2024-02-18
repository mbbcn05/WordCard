package com.babacan05.wordcard.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SentenceService {
    @Headers(
        "X-RapidAPI-Key: df0e0b9ff7msh5e0b3c6e26ff767p1b6b6bjsn264a19ea3f64",
        "X-RapidAPI-Host: models3.p.rapidapi.com",
        "Content-Type: application/json"
    )
    @POST("/")
    suspend fun getSentence(@Query("model_id") modelId: Int, @Query("prompt") prompt: String): ApiResponse
}


val retrofitSentence = Retrofit.Builder()
    .baseUrl("https://models3.p.rapidapi.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

val service = retrofitSentence.create(SentenceService::class.java)

suspend fun giveSentence(modelId: Int=5,text:String="serial",language:String="'english'"):String{

    try {
        val response = service.getSentence(modelId, "give me just a $language sentence with $text")
if("Unfortunately" in response.content || "error" in response.content){ throw Exception()}else{
        return response.content}
    } catch (e: Exception) {
        if(modelId==5){return giveSentence(27,text,language)
        }else  if(modelId==27){
            return giveSentence(20, text, language)
        }else
            return getSamplewithUrban((text))
    }

}

data class ApiResponse(
    val message: String,
    val code: Int,
    val content: String
)
