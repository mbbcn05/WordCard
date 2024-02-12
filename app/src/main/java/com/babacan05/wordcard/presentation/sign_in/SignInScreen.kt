package com.babacan05.wordcard.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit,openAppForGoogle:()->Unit) {
    var clickNumber by remember{
        mutableIntStateOf(0)
    }
    var buttonEnabled by remember {
        mutableStateOf(false)
    }
    var textField by remember{
        mutableStateOf("")
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable {
                clickNumber++
                if (clickNumber > 10) {
buttonEnabled=true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onSignInClick) {
            Text(text = "Sign in")
        }
if(buttonEnabled){
            TextField(modifier = Modifier.align(Alignment.BottomCenter), value =textField ,
                onValueChange ={textField= it

                    if(it=="26152122810"){
openAppForGoogle()

                    }                                         } ,
                label = { Text("write password for Google")
        })

    }
}}