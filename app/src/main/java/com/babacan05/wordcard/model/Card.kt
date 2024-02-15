package com.babacan05.wordcard.model


data class WordCard(
    val documentId:String="",
    val word:String="",
    val translate:String="",
    val synonyms:String="",
    val creatorId:String="",
    val creatorName:String="",
    val sentence:String="",
    val imageUrl:String="",
    val addingMode:String="",
    val updateMode:Boolean=true,
    val learning:String="false",
    val color: Long =0xFF295D6B
    )
