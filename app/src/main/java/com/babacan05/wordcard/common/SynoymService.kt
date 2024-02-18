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
    .addConverterFactory(GsonConverterFactory.create())
    .build()


suspend fun getMySynonym(text:String) :String{
    val service = retrofitSynonym.create(SynonymService::class.java)

    val apiKey = "df0e0b9ff7msh5e0b3c6e26ff767p1b6b6bjsn264a19ea3f64"
    val apiHost = "english-synonyms.p.rapidapi.com"


    try {
        val response = service.getSynonyms(apiKey, apiHost, text)
        var synonys= ""
        response.synonyms.forEachIndexed() {index,asd-> if(index<5){synonys= "$synonys$asd,"
        } }
        return synonys
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}






