package com.babacan05.wordcard.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SynonymService {
    @GET("{word}")
    suspend fun getSynonyms(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") apiHost: String,
        @Path("word") word: String
    ): SynonymResponse
}
data class SynonymResponse(
    val synonyms: List<String>
)

val retrofitSynonym = Retrofit.Builder()
    .baseUrl("https://english-synonyms.p.rapidapi.com/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


suspend fun getMySynonym(text:String) :String{
    val service = retrofitSynonym.create(SynonymService::class.java)

    val apiKey = "****************"
    val apiHost = "english-synonyms.p.rapidapi.com"


    try {
        val response = service.getSynonyms(apiKey, apiHost, text)
        val synonyms = response.synonyms

        val sb = StringBuilder()

        for ((index, word) in synonyms.withIndex()) {
            sb.append(word)
            if (index < 4 && index < synonyms.size - 1) {
                sb.append(", ")
            }
            if (index == 4) {
                break
            }
        }

        return sb.toString()
    }  catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}






