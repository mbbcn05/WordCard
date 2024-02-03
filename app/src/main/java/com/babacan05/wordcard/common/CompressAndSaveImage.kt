package com.babacan05.wordcard.common
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.util.Log

 suspend fun compressAndSaveImage(context: Context, originalUri: Uri, targetFileSizeMb: Double): Uri? {
    try {
        // Glide ile resmi ölçeklendir
        val bitmap = Glide.with(context)
            .asBitmap()
            .load(originalUri)
            .apply(RequestOptions().override(800, 800)) // İsteğe bağlı: Resmi ölçeklendirebilirsiniz.
            .submit()
            .get()

        // Hedef dosya boyutuna kadar sıkıştır
        val quality = calculateQualityForTargetSize(bitmap.byteCount, (targetFileSizeMb * 1024 * 1024).toInt())
        val compressedBitmap = compressBitmap(bitmap, quality)

        // Sıkıştırılmış resmi kaydet
        val outputFile = File(context.cacheDir, "compressed_image.jpg")
        val outputStream: OutputStream = FileOutputStream(outputFile)
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(outputFile)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

private fun calculateQualityForTargetSize(originalSize: Int, targetSize: Int): Int {
    var quality = 100
    while (calculateFileSizeInMb(originalSize, quality) > targetSize) {
        quality -= 10
        print("compress uygulandı $quality")
    }
    return quality
}

private fun calculateFileSizeInMb(byteCount: Int, quality: Int): Float {
    return byteCount * quality / 8f / 1024 / 1024
}

 private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    val byteArray = outputStream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}


suspend fun compressImageToByteArray(context: Context, originalUri: Uri, targetFileSizeMb: Double): ByteArray? {
    try {
        // Glide ile resmi ölçeklendir
        val bitmap = Glide.with(context)
            .asBitmap()
            .load(originalUri)
            .apply(RequestOptions().override(800, 800)) // İsteğe bağlı: Resmi ölçeklendirebilirsiniz.
            .submit()
            .get()

        // Hedef dosya boyutuna kadar sıkıştır
        val quality = calculateQualityForTargetSize(bitmap.byteCount, (targetFileSizeMb * 1024 * 1024).toInt())
        val compressedBitmap = compressBitmap(bitmap, quality)

        // Sıkıştırılmış resmi kaydet
        val byteArrayOutputStream = ByteArrayOutputStream()
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return byteArray
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

