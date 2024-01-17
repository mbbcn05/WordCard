package com.babacan05.wordcard.presentation.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


sealed class BottomNavScreens(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavScreens("home", Icons.Default.Home, "Home")
    object Games : BottomNavScreens("games", Icons.Default.Star, "Games")
    object Notifications : BottomNavScreens("notifications", Icons.Default.Notifications, "Notifications")
    object Settings : BottomNavScreens("settings", Icons.Default.Settings, "Settings")
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home screen")
    }
}

@Composable
fun DashboardScreen() {
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

@Composable
fun BottomNav() {
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
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(text = "Dashboard") },
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
        NavHost(
            navController = navController,
            startDestination = BottomNavScreens.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreens.Home.route) { HomeScreen() }
            composable(BottomNavScreens.Games.route) { DashboardScreen() }
            composable(BottomNavScreens.Notifications.route) { NotificationsScreen() }
            composable(BottomNavScreens.Settings.route) { SettingsScreen() }
        }
    }
}




