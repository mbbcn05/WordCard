package com.babacan05.wordcard.common

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

data class ApiResponseImages(val results: List<Result>)

data class Result(
    val image: String,
    val by: String,
    val download: String,
    val source: String,
    val differentSizes: List<String>,
    val id: String
)


// API arayüzü
interface ApiService {
    @GET("/images/wallpaper")
    suspend fun getImages(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") apiHost: String
    ): ApiResponseImages
}

suspend fun  getImageUrl(text:String):String {
    try {


        val retrofitImages = Retrofit.Builder()
            .baseUrl("https://free-images-api.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // OkHttp istemciyi burada özelleştirebilirsiniz
            .build()

// Retrofit tarafından oluşturulan API servisi
        val serviceImages = retrofitImages.create(ApiService::class.java)

// API çağrısını gerçekleştir
        return serviceImages.getImages(
            apiKey = "df0e0b9ff7msh5e0b3c6e26ff767p1b6b6bjsn264a19ea3f64",
            apiHost = "free-images-api.p.rapidapi.com"
        ).results[0].image
    }catch (e:Exception){
        return e.message.toString()

    }
}


