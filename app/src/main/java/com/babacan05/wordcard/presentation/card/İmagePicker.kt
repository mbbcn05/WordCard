package com.babacan05.wordcard.presentation.card

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.babacan05.wordcard.common.compressAndSaveImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun  ImagePicker(context: Context, enabled: Boolean, onUriSaved: (Uri) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {


            coroutineScope.launch{
                    val myUri=withContext(Dispatchers.Default) {
                        compressAndSaveImage(context =context ,uri,0.5)}
                myUri?.let(onUriSaved)

}
        }
    }
    OutlinedButton(enabled=enabled,onClick = { getContent.launch("image/*") }) {
        Text("Select an image")
    }
}
//Orjinal master güncellendi şimdilik
