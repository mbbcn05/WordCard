package com.babacan05.wordcard

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.babacan05.wordcard.presentation.card.BottomNav
import com.babacan05.wordcard.presentation.card.CardViewModel
import com.babacan05.wordcard.presentation.card.StudyScreen
import com.babacan05.wordcard.presentation.profile.ProfileScreen
import com.babacan05.wordcard.presentation.sign_in.GoogleAuthUiClient
import com.babacan05.wordcard.presentation.sign_in.SignInScreen
import com.babacan05.wordcard.presentation.sign_in.SignInViewModel
import kotlinx.coroutines.launch







@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WorCardApp(googleAuthUiClient: GoogleAuthUiClient){

    val lifecycleOwner = LocalContext.current as? ComponentActivity

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        navigation(
            startDestination = "sign_in",
            route = "login"
        ) {

            composable("sign_in") {
                val viewModel =  it.sharedViewModel<SignInViewModel>(navController)
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(key1 = Unit) {
                    if (googleAuthUiClient.getSignedInUser() != null) {
                        navController.navigate("profile")
                    }
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if (result.resultCode == ComponentActivity.RESULT_OK) {
                            lifecycleOwner?.lifecycleScope?.launch {
                                val signInResult = googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                            }
                        }
                    }
                )

                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if (state.isSignInSuccessful) {
                        Toast.makeText(
                            lifecycleOwner,
                            "Sign in successful",
                            Toast.LENGTH_LONG
                        ).show()

                        navController.navigate("profile")
                        viewModel.resetState()
                    }
                }

                SignInScreen(
                    state = state,
                    onSignInClick = {
                        lifecycleOwner?.lifecycleScope?.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    },{
                        navController.navigate("word_card"){
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        lifecycleOwner?.lifecycleScope?.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(
                                lifecycleOwner,
                                "Signed out",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.popBackStack()
                        }
                    },
                    card_app = {navController.navigate("word_card"){
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                    }
                )
            }

        }
        navigation( startDestination = "app_screen",
            route = "word_card"){

            composable("app_screen"){
                val viewModel =  it.sharedViewModel<CardViewModel>(navController)
Box(modifier = Modifier.fillMaxSize()){
    //Image(alignment = Alignment.TopCenter, painter = painterResource(id = R.drawable.hd), contentDescription ="" , modifier = Modifier.fillMaxSize())

    Image( painter = painterResource(id = R.drawable.water), contentDescription ="" , modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
    //Image(alignment = Alignment.BottomCenter, painter = painterResource(id = R.drawable.hd), contentDescription ="" , modifier = Modifier.fillMaxSize())
               BottomNav(viewModel) {
                   navController.navigate("study_screen") {
                       popUpTo("app_screen") {
                           inclusive = false
                       }
                   }
               }
               }



            }
            composable("study_screen"){
                val viewModel =  it.sharedViewModel<CardViewModel>(navController)
              Box(modifier = Modifier.fillMaxSize()){
                  Image( painter = painterResource(id = R.drawable.water), contentDescription ="" , modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)

                  StudyScreen(viewModel) { navController.navigate("word_card"){
                    popUpTo("word_card") {
                        inclusive = false
                    }
                } }
            }}

            }

        }
    }





@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}