package com.babacan05.wordcard.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UrbanDictionaryService {
    @GET("define")
    suspend fun defineTerm(@Query("term") term: String,
                           @Header("X-RapidAPI-Key") apiKey: String="********",
                           @Header("X-RapidAPI-Host") host: String="mashape-community-urban-dictionary.p.rapidapi.com"): DefinitionResponse
}
val retrofitUrban = Retrofit.Builder()
    .baseUrl("https://mashape-community-urban-dictionary.p.rapidapi.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

val serviceUrban = retrofitUrban.create(UrbanDictionaryService::class.java)
suspend fun getSamplewithUrban(text:String="dear"):String {
    try {
        val response = serviceUrban.defineTerm(text)
        return response.list[0].example.replace("[", "").replace("]", "")
    } catch (e: Exception) {

        return ""

    }
}



data class Definition(
    val definition: String,
    val permalink: String,
    val thumbs_up: Int,
    val author: String,
    val word: String,
    val defid: Int,
    val current_vote: String,
    val written_on: String,
    val example: String,
    val thumbs_down: Int
)

data class DefinitionResponse(
    val list: List<Definition>
)
