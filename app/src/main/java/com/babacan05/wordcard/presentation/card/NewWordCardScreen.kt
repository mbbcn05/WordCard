package com.babacan05.wordcard.presentation.card

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.babacan05.wordcard.model.WordCard
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NewWordCardScreen(onFinish: () -> Unit,viewModel: CardViewModel,wordCard: WordCard= WordCard()) {
    var word by rememberSaveable { mutableStateOf(wordCard.word) }
    var translate by rememberSaveable { mutableStateOf(wordCard.translate) }
    var sampleSentence by rememberSaveable { mutableStateOf(wordCard.sentence) }
    var synonyms by rememberSaveable { mutableStateOf(wordCard.synonyms) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        // Geri tuşu için Row ve IconButton kullanımı
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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                    keyboardType=KeyboardType.Text),
                    keyboardActions = KeyboardActions(onNext = null),
                    isError = word.isEmpty(),
                    value = word,
                    onValueChange = {
                        word = it.filter { it.isWhitespace()||it.isLetter()  }.lowercase()
                    },
                    label = { Text("Enter a new word ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                    keyboardType=KeyboardType.Text),
                    keyboardActions = KeyboardActions(onNext = null),isError = translate.isEmpty(),
                    value = translate,
                    onValueChange = {
                        translate = it.filter { it.isWhitespace()|| it.isLetter()}.lowercase()
                    },
                    label = { Text("Enter a translate") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                    keyboardType=KeyboardType.Text),
                    keyboardActions = KeyboardActions(onNext = null),
                    value = sampleSentence,
                    onValueChange = {
                        sampleSentence = it.filter { it.isWhitespace()|| it.isLetter() }.lowercase()
                    },
                    label = { Text("Enter a sample sentence") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                    keyboardType=KeyboardType.Text),
                    keyboardActions = KeyboardActions(onNext = null),
                    value = synonyms,
                    onValueChange = {
                        synonyms =it.filter { it.isWhitespace()|| it.isLetter() }.lowercase()
                    },
                    label = { Text("Enter a synonym") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(enabled = !word.isEmpty()&&!translate.isEmpty(), modifier=Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.viewModelScope.launch {



                            viewModel.addWordCard(
                                WordCard(
                                    word = word.trim(),
                                    translate = translate.trim(),
                                    sentence = sampleSentence.trim(),
                                    synonyms = synonyms.trim()
                                )
                            )


                        }
                    onFinish()









                    }
                ) {
                    Text("Save WordCard")
                }
            }
        }
    }
}