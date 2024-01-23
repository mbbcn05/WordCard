package com.babacan05.wordcard.presentation.card

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.babacan05.wordcard.model.WordCard


sealed class BottomNavScreens(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavScreens("home", Icons.Default.Home, "Home")
    object Games : BottomNavScreens("games", Icons.Default.Star, "Games")
    object Notifications : BottomNavScreens("notifications", Icons.Default.Notifications, "Notifications")
    object Settings : BottomNavScreens("settings", Icons.Default.Settings, "Settings")
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(viewModel: CardViewModel, navController: NavHostController,state:LazyListState) {

    var wordList=viewModel.wordcardstateFlow.collectAsStateWithLifecycle().value


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {


            LazyColumn(state = state){
                items(wordList){



                    WordCardItem(wordCard = it) {
                        viewModel.updateViewingWordCard(it)
            navController.navigate("WordCardViewScreen")
                    }


                }

}
        
            FloatingActionButton(
            onClick = {
                viewModel.updateViewingWordCard(WordCard())
                navController.navigate("NewCardScreen")

            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
        }
    }


@Composable
fun GameScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Dashboard screen")
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
                val currentRoute =navBackStackEntry?.arguments?.getString("androidx.navigation.dynamicfeatures.FragmentNavigator.Destination")
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(text = "Home") },
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
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text(text = "Games") },
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
                    label = { Text(text = "Notifications") },
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
                    label = { Text(text = "Settings") },
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
        NavHost(
            navController = navController,
            startDestination = BottomNavScreens.Home.route,
            modifier = Modifier.padding(innerPadding)

        ) {


            composable(BottomNavScreens.Home.route) {

                HomeScreen(viewModel,navController,lazyListState)
               }
            composable(BottomNavScreens.Games.route) { GameScreen() }
            composable(BottomNavScreens.Notifications.route) { NotificationsScreen() }
            composable(BottomNavScreens.Settings.route) { SettingsScreen() }
            composable("NewCardScreen") {

                NewWordCardScreen(
                    onFinish = { navController.navigate(BottomNavScreens.Home.route)
                               },
                    viewModel = viewModel,
                    wordCard = viewModel.viewingWorCard.value
                )
            }
            composable("WordCardViewScreen"){ WordCardViewScreen(
                onFinish = {navController.navigate(BottomNavScreens.Home.route)},
                wordCard = viewModel.viewingWorCard.value,
                editClick = { navController.navigate("NewCardScreen") })}
        }
    }
}




