package com.babacan05.wordcard.common

import android.content.Context
import android.content.SharedPreferences
import com.babacan05.wordcard.model.WordCard
import com.google.common.reflect.TypeToken
import com.google.gson.Gson





class MySharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveWordCard(wordCard: WordCard) {
        val updatedList = getWordCardList().toMutableList()
        updatedList.add(wordCard.copy(documentId = getWordCardDocumentId()))
        updateWordCardList(updatedList)
        print("wordcard eklendi")
    }

    private fun updateWordCardList(wordCardList: List<WordCard>) {
        val json = gson.toJson(wordCardList)
        sharedPreferences.edit().putString("WORDCARD", json).apply()
    }

    fun getWordCardList(): List<WordCard> {
        val json = sharedPreferences.getString("WORDCARD", null)
        return if (json != null) {
            val type = object : TypeToken<List<WordCard>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun updateWordCard(wordCard: WordCard) {
        val wordCards = getWordCardList().toMutableList()
        val updatingWordCard = wordCards.find { it.documentId == wordCard.documentId }
        updatingWordCard?.let {
            wordCards.remove(it)
            wordCards.add(wordCard)
            updateWordCardList(wordCards)
        }
    }

    fun deleteWordCard(wordCard: WordCard) {
        val wordCards = getWordCardList().toMutableList()
        val deletingWordCard = wordCards.find { it.documentId == wordCard.documentId }
        deletingWordCard?.let {
            wordCards.remove(it)
            updateWordCardList(wordCards)
        }
    }

    private fun getWordCardDocumentId(): String {
        val json = sharedPreferences.getString("CARDID", null)
        val returningId = if (json != null) {
            gson.fromJson(json, String::class.java)
        } else {
            "0"
        }
        updateWordCardDocumentId((returningId.toInt() + 1).toString())
        return returningId
    }

    private fun updateWordCardDocumentId(id: String) {
        val json = gson.toJson(id)
        sharedPreferences.edit().putString("CARDID", json).apply()
    }

    fun resetSharedPrefWordCards() {
        sharedPreferences.edit().remove("WORDCARD").apply()
        sharedPreferences.edit().remove("CARDID").apply()
    }
}
