package com.babacan05.wordcard.presentation.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.babacan05.wordcard.model.WordCard

@Composable
fun WordCardItem(wordCard: WordCard,onClick:()->Unit) {

    Card(
        modifier = Modifier.padding(8.dp)
            .clickable { onClick()  },
        backgroundColor = Color.LightGray,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Word: ${wordCard.word}", fontWeight = FontWeight.Bold)
            Text(text = "Definition: ${wordCard.translate}")
        }

    }
}