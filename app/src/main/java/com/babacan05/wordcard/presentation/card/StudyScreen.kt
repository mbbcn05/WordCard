package com.babacan05.wordcard.presentation.card
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.babacan05.wordcard.model.WordCard
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StudyScreen(viewModel: CardViewModel) {

    val wordCards = viewModel.offlineWordCards.collectAsState().value.toMutableList().shuffled().filter { it.learning=="false" }
    var viewingcardNumber by rememberSaveable {
        mutableIntStateOf(0)
    }

    Box(modifier = Modifier) {

val context= LocalContext.current
if(wordCards.size>0){
        Question(modifier=Modifier.fillMaxSize(),wordCard = wordCards[viewingcardNumber],viewModel) {

            if (viewingcardNumber < wordCards.size - 1) {
                viewingcardNumber++

            }else{

                Toast.makeText(context ,"You have seen all the list...", Toast.LENGTH_LONG).show()
                viewingcardNumber=0
            }}
        }
else{
        Text(text = "There is no word marked as being studied")
    }
    }
}








@Composable
fun Question(
    modifier: Modifier,
    wordCard: WordCard,
    viewModel: CardViewModel,

    nextquestion: () -> Unit
){
    var learned by remember {mutableStateOf(false)}

    LaunchedEffect(key1 =wordCard) {
        viewModel.getİsLearned(wordCard.documentId){
            learned=it
            if(it){nextquestion()}else{learned=false}
        }
    }

    var showMessage by rememberSaveable {
        mutableStateOf(false)
    }
    val context= LocalContext.current

var trueAnswer by rememberSaveable {
    mutableStateOf(false)
}

    var gameOver by remember{
        mutableStateOf(false)
    }
    var userAnswer by rememberSaveable {
        mutableStateOf("")
    }
    var currentTime by remember{
        mutableStateOf(60000L)
    }
    if (showMessage) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                Button(onClick = { showMessage = false }) {
                    Text("Close")

                }
            }
        ) {
            Text(text = wordCard.synonyms+wordCard.sentence)
        }
    }
    Box(modifier=modifier.padding(top = 110.dp,end=0.dp,start=0.dp,bottom=0.dp)){

       if (!trueAnswer&&!gameOver){
    Timer(isTimerRunning =!gameOver,currentTime = currentTime,modifier= Modifier
        .align(Alignment.TopCenter)
        .size(150.dp)) {
        gameOver = true
    }
    }
           if(gameOver){
             Image(painter = painterResource(id =com.babacan05.wordcard.R.drawable.pngegg), contentDescription ="",modifier= Modifier
                 .align(Alignment.TopCenter ))
           }
if(!gameOver&&trueAnswer){
    Image(painter = painterResource(id =com.babacan05.wordcard.R.drawable.checktrue), contentDescription ="" ,modifier= Modifier
        .align(Alignment.TopCenter ))
}






Box(modifier=Modifier.align(Alignment.Center)) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = wordCard.word.uppercase(),
            fontSize = if (wordCard.word.length < 5) 100.sp else if(wordCard.word.length < 7)80.sp else if(wordCard.word.length < 11)40.sp else 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,

        )


        Spacer(modifier = Modifier.size(10.dp))
        OutlinedTextField(enabled=!trueAnswer&&!gameOver,isError =!trueAnswer,label={ Text(text = "Give your answer")},value = userAnswer, onValueChange = {
           if(userAnswer!=wordCard.translate&&!gameOver){ userAnswer = it}
            trueAnswer = if (it == wordCard.translate) {
                true
            } else {
                false
            }

        })
        Spacer(modifier = Modifier.size(10.dp))
        Row {


        Button(onClick = {
            userAnswer=wordCard.translate
            gameOver=true
            trueAnswer=false

             }) {
            Text(
                text =
                "Show the Ansver")
        }
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Checkbox(checked = learned, onCheckedChange ={learned=!learned
                   // viewModel.updateIsLearned(wordCard,learned, context = context)
                    viewModel.updateofflineWordCard(wordCard.copy(learning = if(it){"true"}else{"false"}))
                })
                Text(fontSize = 10.sp,text = "Learned")
            }

            Button(onClick = {
            nextquestion()

            userAnswer = ""
            trueAnswer = false
                gameOver=false
                currentTime=60000L
        }, enabled = trueAnswer||gameOver) {
            Text(text = "Next Question")
        }}
    }

}
    }

}

@Composable
fun Timer(
    modifier: Modifier = Modifier,
    initialValue: Float = 1f,
    strokeWidth: Dp = 5.dp,
    currentTime: Long,
    isTimerRunning: Boolean,
    onTimerFinished: () -> Unit // Geri sayım sıfıra ulaştığında çalışacak fonksiyon
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var value by remember { mutableStateOf(initialValue) }
    var time by remember { mutableStateOf(currentTime) }
    LaunchedEffect(currentTime, isTimerRunning) {
       // var time = currentTime
        var running = isTimerRunning
        while (time > 0 && running) {
            delay(50L)
            time -= 100L
            value = time / 60000f // Saniye cinsinden
        }
        if (time <= 0) {
            onTimerFinished() // Geri sayım sıfıra ulaştığında belirtilen fonksiyonu çağır
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                size = it
            }
    ) {
        Canvas(modifier = modifier) {
            drawArc(
                color = Color.Red,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = if (isTimerRunning) Color.Blue else Color.Red,
                startAngle = -215f,
                sweepAngle = 250f * value,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            val center = Offset(size.width / 2f, size.height / 2f)
            val beta = (250f * value + 145f) * (PI / 180f).toFloat()
            val r = size.width / 2f
            val a = cos(beta) * r
            val b = sin(beta) * r
            drawPoints(
                listOf(Offset(center.x + a, center.y + b)),
                pointMode = PointMode.Points,
                color = Color.Red,
                strokeWidth = (strokeWidth * 6f).toPx(),
                cap = StrokeCap.Round
            )
        }

    }
}