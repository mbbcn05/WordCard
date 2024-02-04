package com.babacan05.wordcard.presentation.card

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.babacan05.wordcard.common.compressAndSaveImage
import com.babacan05.wordcard.common.compressImageToByteArray
import com.babacan05.wordcard.common.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun  ImagePicker(context: Context, onImageSelected: (ByteArray) -> Unit,onUriSaved: (Uri)-> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {

if (isInternetAvailable(context)){


            coroutineScope.launch {
                val compressedByteArray = withContext(Dispatchers.Default) {
                    print("HEDEF BÖLÜME GİRİLDİ")
                    compressImageToByteArray(context, uri, 0.5)
                }
                compressedByteArray?.let(onImageSelected)
                print("compress başarılı")
            }




            }else{
                coroutineScope.launch{
                    val myUri=withContext(Dispatchers.Default) {
                        compressAndSaveImage(context =context ,uri,0.5)}
                myUri?.let(onUriSaved)
                }
}
        }
    }
    // Resim seçme butonu
    Button(onClick = { getContent.launch("image/*") }) {
        Text("Select an image")
    }
}