package com.babacan05.wordcard.presentation.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.babacan05.wordcard.R
import com.babacan05.wordcard.model.WordCard

@Composable
fun WordCardSearchItem(wordCard: WordCard,onClick:()->Unit) {

    Card(
        modifier = Modifier.padding(8.dp)
            .clickable { onClick() }.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
    ) {
        Box( contentAlignment = Alignment.Center,
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.LightGray,
                        Color.Gray
                    )
                )
            )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(wordCard.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.rounded_globe_24),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = wordCard.word.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0f))
                )
                Text(
                    text = wordCard.translate,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0f))
                )
            }
        }
    }
}