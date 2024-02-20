package com.babacan05.wordcard.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Veri sınıfları (data classes) JSON yanıtını temsil eder
data class SearchParameters(
    val q: String,
    val gl: String,
    val hl: String,
    val type: String,
    val num: Int,
    val autocorrect: Boolean,
    val page: Int,
    val engine: String
)

data class Image(
    val title: String,
    val imageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val thumbnailUrl: String,
    val thumbnailWidth: Int,
    val thumbnailHeight: Int,
    val source: String,
    val domain: String,
    val link: String,
    val googleUrl: String,
    val position: Int
)

data class SearchResponse(
    val searchParameters: SearchParameters,
    val images: List<Image>
)

// API servisini tanımla
interface GoogleImageService {
    @POST("/images")
    suspend fun searchImages(
        @Header("X-API-KEY") apiKey: String,
        @Body request: SearchParameters
    ): SearchResponse
}

val retrofitSerper = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://google.serper.dev/")
    .client(okHttpClient)
    .build()


suspend fun getImagewithSerper(text:String):String{
try {
    val serviceSerper = retrofitSerper.create(GoogleImageService::class.java)
    val searchParameters = SearchParameters(
        q = text,
        gl = "us",
        hl = "en",
        type = "images",
        num = 10,
        autocorrect = false,
        page = 1,
        engine = "google"
    )

    val response = serviceSerper.searchImages("9cc7f54627e93b1f06771d5fd03ec3d7a9f99c0a", searchParameters)
    return response.images[0].imageUrl

}catch (e:Exception){
    return e.message.toString()
}


}

