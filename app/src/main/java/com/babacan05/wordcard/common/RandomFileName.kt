package com.babacan05.wordcard.common

import java.util.*

fun generateRandomFileName(): String {
    val random = Random()
    val alphabet = "abcdefghijklmnopqrstuvwxyz"
    val fileNameLength = 10 // İstediğiniz uzunlukta dosya ismi için uygun bir değer belirleyebilirsiniz.

    return (1..fileNameLength)
        .map { alphabet[random.nextInt(alphabet.length)] }
        .joinToString("")
}