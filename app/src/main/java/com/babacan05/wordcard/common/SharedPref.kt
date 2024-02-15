package com.babacan05.wordcard.common

import android.content.Context
import android.content.SharedPreferences
import com.babacan05.wordcard.model.WordCard
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.Serializable
import java.util.concurrent.TimeUnit



class MySharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()


    fun updateSettings(settings:Settings){
        val json = gson.toJson(settings)
        sharedPreferences.edit().putString("settings", json).apply()



    }

    fun getSettings():Settings{
        val json = sharedPreferences.getString("settings", null)
        return if (json != null) {
            val type = object : TypeToken<Settings>() {}.type
            gson.fromJson(json, type)
        } else {
           Settings()
        }

    }

}


data class Settings(
    val reminderMode:Boolean=false,
    val repeatinterval:Long=1,
    val timeUnit:TimeUnit=TimeUnit.DAYS,
    val arrangementOfWords:String="sortedByNames"): Serializable

