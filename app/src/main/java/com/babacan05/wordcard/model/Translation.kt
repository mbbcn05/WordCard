package com.babacan05.wordcard.model

data class TranslationResult(
    val detectedLanguage: DetectedLanguage,
    val translations: List<Translation>
)

data class DetectedLanguage(
    val language: String,
    val score: Int
)

data class Translation(
    val text: String,
    val to: String
)
data class TranslationRequestBody(
    val Text: String
)