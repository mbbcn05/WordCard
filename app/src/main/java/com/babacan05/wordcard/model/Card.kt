package com.babacan05.wordcard.model



data class WordCard(val name:String,
    val translate:String,
                    val synonym:String,

    val username:String?=null,
    val sentence:String?=null,
    val imageUrl:String?=null,
    )
