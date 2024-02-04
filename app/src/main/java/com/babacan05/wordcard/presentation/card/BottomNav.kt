package com.babacan05.wordcard.presentation.card

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.babacan05.wordcard.R
import com.babacan05.wordcard.model.WordCard
import kotlinx.coroutines.launch


sealed class BottomNavScreens(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavScreens("home", Icons.Default.Home, "Home")
    object Games : BottomNavScreens("games", Icons.Default.Star, "Games")
    object Notifications : BottomNavScreens("notifications", Icons.Default.Notifications, "Notifications")
    object Settings : BottomNavScreens("settings", Icons.Default.Settings, "Settings")
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(viewModel: CardViewModel, navController: NavHostController,state:LazyListState) {
    val context: Context = LocalContext.current

    //var checkingmigratewords by remember { mutableIntStateOf(0 ) }
    var searchQuery by remember { mutableStateOf("") }
    val offlinelist=viewModel.offlineWordCards.collectAsStateWithLifecycle().value


    val filteredWordList by remember(offlinelist, searchQuery) {
        derivedStateOf {
           viewModel.filterWordList(offlinelist, searchQuery)
       }
    }



    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search TextField
            OutlinedTextField(keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                keyboardType= KeyboardType.Text, imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onNext = null),
                value = searchQuery,
                onValueChange = {
                    searchQuery = it

                },
                singleLine = true,
                label = { Text("Search in Your Words") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            LazyColumn(state = state) {
                items(filteredWordList) {



                    WordCardItem(wordCard = it) {
           // checkingmigratewords++
                        viewModel.updateViewingWordCard(it)
                        navController.navigate("WordCardViewScreen")
                    }


                }

            }



        }
        FloatingActionButton(
            onClick = {
               // checkingmigratewords++
                viewModel.updateViewingWordCard(WordCard())
                navController.navigate("NewCardScreen")

            },
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun GameScreen(viewModel: CardViewModel, navController: NavHostController,state:LazyListState) {

    val wordList = viewModel.wordcardSearchstateFlow.collectAsStateWithLifecycle().value
    var searchQuery by remember { mutableStateOf("") }
    LaunchedEffect(key1 = searchQuery) {
    viewModel.searchWordCardOnline(searchQuery)
}
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search TextField
            OutlinedTextField(keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                keyboardType= KeyboardType.Text, imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onNext = null),
                value = searchQuery,
                onValueChange = {
                    searchQuery = it.lowercase()
                    //viewModel.searchWordCardOnline(it)

                },
                singleLine = true,
                label = { Text("Search in Online") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            LazyColumn(state = state) {
                items(wordList) {



                    WordCardSearchItem(wordCard = it) {
                        viewModel.updateViewingWordCard(it)
                        navController.navigate("WordCardSearchViewScreen")
                    }


                }

            }

        }
    }
}

@Composable
fun NotificationsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Notifications screen")
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Settings screen")
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BottomNav(viewModel: CardViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute =
                    navBackStackEntry?.arguments?.getString("androidx.navigation.dynamicfeatures.FragmentNavigator.Destination")
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { "" },
                    selected = currentRoute == BottomNavScreens.Home.route,
                    onClick = {
                        navController.navigate(BottomNavScreens.Home.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                )

                // Dashboard Tab
                BottomNavigationItem(
                    icon = {   Icon(painterResource(R.drawable.rounded_globe_24), contentDescription = null)},
                    label = { ""},
                    selected = currentRoute == BottomNavScreens.Games.route,
                    onClick = {
                        navController.navigate(BottomNavScreens.Games.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                )

                // Notifications Tab
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = {"" },
                    selected = currentRoute == BottomNavScreens.Notifications.route,
                    onClick = {
                        navController.navigate(BottomNavScreens.Notifications.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                )

                // Settings Tab
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = {""  },
                    selected = currentRoute == BottomNavScreens.Settings.route,
                    onClick = {
                        navController.navigate(BottomNavScreens.Settings.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val lazyListState = rememberLazyListState()
        val lazyListSearchState = rememberLazyListState()
        NavHost(
            navController = navController,
            startDestination = BottomNavScreens.Home.route,
            modifier = Modifier.padding(innerPadding)

        ) {


            composable(BottomNavScreens.Home.route) {

                HomeScreen(viewModel, navController, lazyListState)
            }
            composable(BottomNavScreens.Games.route) { GameScreen(viewModel,navController,lazyListSearchState) }
            composable(BottomNavScreens.Notifications.route) { NotificationsScreen() }
            composable(BottomNavScreens.Settings.route) { SettingsScreen() }
            composable("NewCardScreen") {

                NewWordCardScreen(
                    onFinish = {
                        navController.navigate(BottomNavScreens.Home.route)
                    },
                    viewModel = viewModel,
                    wordCard = viewModel.viewingWorCard.value
                )
            }
            composable("WordCardViewScreen") {
                WordCardViewScreen(
                    onFinish = { navController.navigate(BottomNavScreens.Home.route) },
                    viewModel = viewModel,
                    wordCard = viewModel.viewingWorCard.value,
                    editClick = { navController.navigate("NewCardScreen") }
                ) { viewModel.viewModelScope.launch { viewModel.deleteWordCard(viewModel.viewingWorCard.value.documentId)

                } }
            }
            composable("WordCardSearchViewScreen"){
                WordCardSearchViewScreen(
                    wordCard = viewModel.viewingWorCard.value,
                    onFinish = { navController.navigate(BottomNavScreens.Home.route)},
                    saveClick = {  viewModel.viewingWorCard.value?.let {word->viewModel.viewModelScope.launch { viewModel.saveWordCard(word) }}})

            }

        }
    }
}




