package com.babacan05.wordcard.presentation.card

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.compose.ImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.babacan05.wordcard.R
import com.babacan05.wordcard.common.isInternetAvailable
import com.babacan05.wordcard.model.WordCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch



@Composable
fun WordCardViewScreen(
    wordCard: WordCard,
    viewModel: CardViewModel,
    onFinish: () -> Unit,
    editClick: () -> Unit,
    modifier: Modifier = Modifier,
    deleteClick: () -> Job,


) {
val context= LocalContext.current
    var learned by rememberSaveable {
        mutableStateOf(wordCard.learning != "false")
    }

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
            IconButton(modifier=Modifier.align(Alignment.Start),onClick = { onFinish() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }



            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .weight(1f, true)
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
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
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(200.dp)
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

                    Text(modifier = Modifier.background(Color.Green.copy(alpha = 0.1f)),
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

                    // Resim seçme butonu




                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.Transparent, shape = RoundedCornerShape(16.dp)
                            )

                    ) {
                        OutlinedButton(shape = RoundedCornerShape(15.dp),
                            onClick = editClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(text = "Edit Card")
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Checkbox(checked = learned, onCheckedChange ={learned=!learned

                                viewModel.updateofflineWordCard(wordCard.copy(learning = if(it){"true"}else{"false"}))
                                if(it) {

                                    Toast.makeText(
                                        context,
                                        "The WordCard is marked as learned",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else{
                                    Toast.makeText(
                                        context,
                                        "The WordCard is marked as being studied",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            })
                            Text(fontSize = 10.sp,text = "Learned")
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedButton(shape = RoundedCornerShape(15.dp),
                            onClick = {

                                deleteClick()
                                onFinish()}
                            ,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(text = "Delete Card")
                        }

                    }
                    }





            }
        }

        }
    }
