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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.babacan05.wordcard.model.WordCard

@Composable
fun WordCardViewScreen(wordCard: WordCard,onFinish:()->Unit,editClick:()->Unit,modifier: Modifier=Modifier) {

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(50.dp),

        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.DarkGray
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { onFinish() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column (modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){

                Text(wordCard.word.uppercase())
                Spacer(modifier = Modifier.height(8.dp))
                Text(wordCard.translate)
                Spacer(modifier = Modifier.height(8.dp))

                Text(wordCard.synonyms)
                Spacer(modifier = Modifier.height(8.dp))

                Text(wordCard.sentence)
                Spacer(modifier = Modifier.height(8.dp))


                Row(){
                    Button(onClick = editClick) {
                        Text(text = "Edit Card")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Delete Card")
                    }
                }


            }


        }
    }


}
@Preview(showSystemUi = true)
@Composable
fun ShowWordCard(){
    WordCardViewScreen(WordCard(),{},{},modifier=Modifier.fillMaxSize())
}
