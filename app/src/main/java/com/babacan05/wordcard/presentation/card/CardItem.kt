package com.babacan05.wordcard.presentation.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.babacan05.wordcard.R
import com.babacan05.wordcard.model.WordCard
import com.plcoding.composegooglesignincleanarchitecture.ui.theme.hilalsColor
import kotlin.math.absoluteValue

@Composable
fun WordCardItem(wordCard: WordCard, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val dragThreshold = 70.dp // Sürükleme eşiği
    var startX by remember { mutableStateOf(0f) } // Sürükleme başlangıç noktası
    var isDragging by remember { mutableStateOf(false) }
    var animate by remember {
        mutableStateOf(false)
    }
    val rotationY = remember { Animatable(0f) }
    var isFlipped by remember { mutableStateOf(false) }

    var rotationright by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(animate) {
        if (animate) {
            if (rotationright) {
                rotationY.animateTo(90f, animationSpec = tween(durationMillis = 500))
                rotationY.snapTo(270f)
                isFlipped = !isFlipped
                rotationY.animateTo(360f, animationSpec = tween(durationMillis = 1000))
                rotationY.snapTo(0f)
            } else {
                rotationY.animateTo(-90f, animationSpec = tween(durationMillis = 500))
                rotationY.snapTo(-270f)
                isFlipped = !isFlipped
                rotationY.animateTo(-360f, animationSpec = tween(durationMillis = 1000))
                rotationY.snapTo(0f)
            }




            animate = false

        }
    }



    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
            .size(200.dp)
            .graphicsLayer(
                rotationY = rotationY.value,
                transformOrigin = TransformOrigin.Center
            )
            .pointerInput(Unit) {
                               detectHorizontalDragGestures { change, dragAmount ->
                                   if (!isDragging) {
                                       // Eğer sürükleme işlemi başlamadıysa, başlangıç noktasını kaydet
                                       startX = change.position.x
                                       isDragging = true // Sürükleme işlemi başladı
                                   }

                                   // Sürükleme mesafesi hesaplanıyor
                                   val dragDistance = (change.position.x - startX).absoluteValue
                                   // Sağa sürükleme hareketi algılandığında
                                   if (dragDistance >= dragThreshold.toPx()) {
                                       rotationright = dragAmount > 0
                                       animate = true
                                       isDragging = false // Sürükleme işlemi tamamlandı
                                   }

                               }





            }
            ,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.background(Color(wordCard.color.toInt()).copy(alpha = 0.6f))
        ) {
            if (isFlipped) {
                Text(
                    text = wordCard.translate.uppercase(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp).align(Alignment.BottomCenter)
                        ,
                    color = Color.White,
                    style = MaterialTheme.typography.h5
                )
            } else{ AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(wordCard.imageUrl)
                    .crossfade(true)
                    .build(),

                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
            Column(
                modifier = modifier


                    .background(Color.Black.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(20.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = wordCard.word.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clip(
                            RoundedCornerShape(15.dp)
                        )//Metnin arkasındaki siyah arka planı kaplayacak şekilde ayarlayın
                )
            }
        }}
    }
}
