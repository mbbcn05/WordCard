package com.babacan05.wordcard.presentation.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.babacan05.wordcard.model.WordCard

@Composable
fun WordCardItem(wordCard: WordCard,modifier: Modifier=Modifier,onClick:()->Unit,) {

    Card(
        modifier = Modifier.padding(8.dp)
            .clickable { onClick() }.size(150.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
    ) {
        Box(contentAlignment = Alignment.Center,
            modifier = modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                         Color.Cyan,Color.Transparent,
                    )
                )
            )
        ) {
            Column(
                modifier = modifier.padding(26.dp),Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = wordCard.word.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

            }
        }
    }
}