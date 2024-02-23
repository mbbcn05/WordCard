package com.babacan05.wordcard.presentation.card

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.babacan05.wordcard.R
import com.babacan05.wordcard.common.getGoogleTranslate
import com.babacan05.wordcard.common.getImagewithSerper
import com.babacan05.wordcard.common.getMySynonym
import com.babacan05.wordcard.common.getTranslate
import com.babacan05.wordcard.common.getTranslator
import com.babacan05.wordcard.common.giveSentence
import com.babacan05.wordcard.common.isInternetAvailable
import com.babacan05.wordcard.model.WordCard
import com.plcoding.composegooglesignincleanarchitecture.ui.theme.hilalsColor
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
enum class TranslationOptions(val abbreviation: String, val nameInLanguage: String,val flagId: Int) {
    NOLANGUAGE("??","Language to translate to",R.drawable.word),
    TURKISH("tr", "Turkish-Türkçe",R.drawable.tr),
    ENGLISH("en", "English",R.drawable.en),
    CHINESE("zh", "Chinese-中文",R.drawable.zh),
    SPANISH("es", "Spanish-Español",R.drawable.es),
    ARABIC("ar", "Arabic-العربية",R.drawable.ar),
    HINDI("hi", "Hindi-हिन्दी",R.drawable.hi),
    FRENCH("fr", "French-Français",R.drawable.fr),
    URDU("ur", "Urdu-اردو",R.drawable.ur),
    PORTUGUESE("pt", "Portuguese-Português",R.drawable.pt),
    BENGALI("bn", "Bengali-বাংলা",R.drawable.bn),
    RUSSIAN("ru", "Russian-Русский",R.drawable.ru),
    JAPANESE("ja", "Japanese-日本語",R.drawable.ja),
    PERSIAN("fa", "Persian-فارسی",R.drawable.fa),
    GERMAN("de", "German-Deutsch",R.drawable.de),
    KOREAN("ko", "Korean-한국어",R.drawable.ko),
    VIETNAMESE("vi", "Vietnamese-Tiếng Việt",R.drawable.vi),
    ITALIAN("it", "Italian-Italiano",R.drawable.it),

    INDONESIAN("id", "Indonesian-Bahasa Indonesia",R.drawable.id),
    KAZAKH("kk", "Kazakh-Қазақ тілі",R.drawable.kk)
}
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NewWordCardScreen(onFinish: () -> Unit,viewModel: CardViewModel,wordCard: WordCard= WordCard()) {
    var word by rememberSaveable { mutableStateOf(wordCard.word) }
    var translate by rememberSaveable { mutableStateOf(wordCard.translate) }
    var sampleSentence by rememberSaveable { mutableStateOf(wordCard.sentence) }
    var synonyms by rememberSaveable { mutableStateOf(wordCard.synonyms) }
    var imageUrl by rememberSaveable {
        mutableStateOf(wordCard.imageUrl)
    }
    val context = LocalContext.current

    var expandedMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedLanguage by remember {
        mutableStateOf<TranslationOptions>(TranslationOptions.NOLANGUAGE)
    }
    val toLanguageSelected by remember(selectedLanguage) {
       derivedStateOf{
           selectedLanguage!=TranslationOptions.NOLANGUAGE
       }

    }

var generateCard by rememberSaveable {
    mutableIntStateOf(0)
}
    var generate by rememberSaveable {
        mutableStateOf(false)
    }
    var loading by rememberSaveable {
        mutableStateOf(false)
    }
    val brightness = remember { Animatable(1f) }
    LaunchedEffect(generate,selectedLanguage) {
        if(generate&&selectedLanguage==TranslationOptions.NOLANGUAGE) {

            brightness.animateTo(
                targetValue = 0.5f, // Parlaklık değerini hedef değere animasyonlu olarak ayarlayın
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 300), // Animasyonun süresini ve türünü ayarlayın
                    repeatMode = RepeatMode.Reverse // Animasyonun nasıl tekrarlanacağını ayarlayın
                )
            )
        }else{brightness.stop()
            brightness.snapTo(1f)}
    }
    LaunchedEffect(key1 = generateCard){
     if(generateCard>0){
         viewModel.viewModelScope.launch {
             val translateDeferred = async { getTranslate(selectedLanguage.abbreviation, word) }
             val sampleSentenceDeferred = async { giveSentence(text = word) }
             val synonymsDeferred = async { getMySynonym(word) }
             val imageUrlDeferred = async { getImagewithSerper(word) }

             // Tüm işlemler paralel olarak çalışacak
             translate = translateDeferred.await()
            //sampleSentence = sampleSentenceDeferred.await()
            // synonyms = synonymsDeferred.await()
          //   imageUrl = imageUrlDeferred.await()

             // Tüm async çağrılar tamamlandıktan sonra loading'i false olarak ayarla
             loading = false

         }
     }
    }


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
            LoadingAnimation(isLoading = loading)
            if(!loading){Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
             Row(){   AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                   // placeholder = painterResource(R.drawable.rounded_globe_24),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                 if(imageUrl.isNotEmpty()){IconButton(onClick ={
                     imageUrl=""}){
                     Icon(imageVector = Icons.Default.Delete, contentDescription = "",tint = LocalContentColor.current)}
                 }
             }

                Row {
                    ImagePicker (LocalContext.current){

                        imageUrl=it.toString()

                    }
Spacer(modifier = Modifier.width(12.dp))
                    AnimatedVisibility(visible = generate){
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Button( enabled=toLanguageSelected,onClick = { if(isInternetAvailable(context)){
                                generateCard++
                                generate=false
                                loading=true
                            }else{
                                Toast.makeText(context,"Please check your connection!",Toast.LENGTH_SHORT).show()
                            } }) {
                                Text(text = "Auto-Generate ")

                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement =Arrangement.Center,modifier=Modifier.clickable { expandedMenu=true }.graphicsLayer {
                                // Parlaklık efektini uygulayın
                                alpha = brightness.value
                            }.background(Color.Cyan, RoundedCornerShape(10.dp)
                            )) {

                                Text(text =selectedLanguage.abbreviation.toUpperCase(),
                                style = TextStyle(fontWeight = FontWeight.ExtraBold)
                                )

                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription ="" )
                                DropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu=false }) {
                                    for(language in TranslationOptions.entries){
                                        DropdownMenuItem(onClick = { selectedLanguage=language
                                            expandedMenu=false}) {
                                          Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
                                              Image(painter = painterResource(id = language.flagId), contentDescription ="" )
                                              Text(text =language.nameInLanguage)
                                              
                                          }

                                        }
                                    }
                                }
                            }
                        }



                    }




                    
                }



                    Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(trailingIcon = {                             Image(modifier = Modifier.clickable {                                 Toast.makeText(
                    context,"When auto generating, the target language will be automatically detected!",Toast.LENGTH_LONG).show()
                }, painter = painterResource(id =TranslationOptions.NOLANGUAGE.flagId), contentDescription ="" )
                },keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                    keyboardType=KeyboardType.Text),
                    keyboardActions = KeyboardActions(onNext = null),
                    isError = word.isEmpty(),
                    value = word,
                    onValueChange = {
                        word = it.filter { it.isWhitespace()||it.isLetter()  }.lowercase()
                        if(it.length>2) {generate=true} else{ generate=false}
                    },
                    label = { Text("Enter a new word ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(trailingIcon = {      if(selectedLanguage!=TranslationOptions.NOLANGUAGE){
                    Image(modifier = Modifier.clickable {
                        Toast.makeText(
                            context,
                            "When auto generating, the language to translate to will be ${selectedLanguage.nameInLanguage}!",
                            Toast.LENGTH_LONG
                        ).show()
                    },painter = painterResource(id =selectedLanguage.flagId), contentDescription ="" )
                }
                },keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
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
                        sampleSentence = it.filter { it.isWhitespace()|| it.isLetter() }
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


               OutlinedButton(enabled = !word.isEmpty()&&!translate.isEmpty(), modifier=Modifier.fillMaxWidth(),
                    onClick = {



                        viewModel.viewModelScope.launch {


                                viewModel.saveWordCard(
                                    WordCard(
                                        word = word.trim(),
                                        translate = translate.trim(),
                                        sentence = sampleSentence.trim(),
                                        synonyms = synonyms.trim(),
                                        documentId = wordCard.documentId,
                                        creatorId = wordCard.creatorId,
                                        imageUrl = imageUrl
                                    ),wordCard.creatorId==viewModel.wordCardUserId
                                )


                            }
                            onFinish()





                    }
                ) {
                    Text("Save WordCard", color = hilalsColor)
                }
            }
        }
    }}
}

@Composable
fun LoadingAnimation(isLoading: Boolean) {
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000),
                repeatMode = RepeatMode.Restart
            ), label = "Its Generating"
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .rotate(rotation),
                color = Color.Blue,
                strokeWidth = 4.dp
            )
        }
    }
}