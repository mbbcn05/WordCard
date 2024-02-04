package com.babacan05.wordcard.presentation.card





import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.babacan05.wordcard.R
import com.babacan05.wordcard.model.WordCard

@Composable
fun WordCardSearchViewScreen(
    wordCard: WordCard,
    onFinish: () -> Unit,
    saveClick: () -> Unit,
    modifier: Modifier = Modifier,

    ) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.DarkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            IconButton(onClick = { onFinish() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }



            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp).weight(1f, true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(wordCard.imageUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.rounded_globe_24),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape).size(200.dp)
                    )
                    Text(
                        text = wordCard.word.uppercase(),
                        fontSize = if (wordCard.word.length < 5) 100.sp else if(wordCard.word.length < 7)80.sp else if(wordCard.word.length < 11)40.sp else 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = wordCard.translate,
                        fontSize = 18.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = wordCard.synonyms,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = wordCard.sentence,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))



                    Button(
                        onClick = {onFinish()
                            saveClick()},
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Text(text = "Save Card")
                    }





                }
            }
        }
    }
}