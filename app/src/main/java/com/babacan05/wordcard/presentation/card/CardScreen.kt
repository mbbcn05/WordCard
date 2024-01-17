package com.babacan05.wordcard.presentation.card

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
    fun CardSaveScreen(saveUser: () -> Unit, value: String){
Button(onClick = saveUser
){
    Text("Save Data")
}
    Text(value)


    }

