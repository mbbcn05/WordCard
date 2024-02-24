package com.babacan05.wordcard.presentation.card
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.babacan05.wordcard.R
import com.babacan05.wordcard.common.ReminderWorker
import com.babacan05.wordcard.model.WordCard
import com.babacan05.wordcard.presentation.Admob.AdMobBanner
import com.plcoding.composegooglesignincleanarchitecture.ui.theme.hilalsColor
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


sealed class BottomNavScreens(val route: String, val icon: ImageVector?, val label: String) {
    object Home : BottomNavScreens("home", Icons.Default.Home, "Home")
    object Search : BottomNavScreens("search", Icons.Default.Star, "Search")
    object Study : BottomNavScreens("study",null, "Study")
    object Settings : BottomNavScreens("settings", Icons.Default.Settings, "Settings")
}


@SuppressLint("RememberReturnType")
@Composable
fun HomeScreen(
    viewModel: CardViewModel,
    navController: NavHostController,
    state: LazyGridState,
    bottomBarVisibility: MutableState<Boolean>

) {
    val context: Context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    val offlinelist = viewModel.offlineWordCards.collectAsStateWithLifecycle().value.sortedBy { it.word }

    val filteredWordList by remember(offlinelist, searchQuery) {
        derivedStateOf {
            viewModel.filterWordList(offlinelist, searchQuery)
        }
    }

    var textFieldVisible by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        viewModel.migrateCardsIntoOnline(context, offlinelist.filter { it.updateMode })
    }

    Box(
        modifier = Modifier.fillMaxSize() ,
        contentAlignment = Alignment.Center
    ) {

        val offset = remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
        var previousOffset by remember { mutableStateOf(0) }
var mydp by remember{
    mutableStateOf(64)
}
        LaunchedEffect(offset.value) {
            textFieldVisible = offset.value <= previousOffset
            bottomBarVisibility.value= offset.value <= previousOffset
            previousOffset = offset.value
            if(state.firstVisibleItemIndex==0){
                if(64-offset.value*3/2>=0) {mydp=64-offset.value*3/2} else{mydp=0}
            }

        }

        val customTextFieldColors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.White.copy(alpha = 0.7f),
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Blue,
            unfocusedIndicatorColor = Color.Black,
            disabledIndicatorColor = Color.Transparent
        )

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 0.dp)

        ) {

            val wordsPerAdBlock = 4
            val adsPerBlock = 2

            LazyVerticalGrid(
                state = state,
                modifier = Modifier
                    .animateContentSize()
                    .padding(start = 0.dp, top = (mydp.dp), bottom = 0.dp, end = 0.dp),
                userScrollEnabled = true,
                columns = GridCells.Fixed(2)
            ) {
                items(count=filteredWordList.size + filteredWordList.size / wordsPerAdBlock * adsPerBlock,key={index ->
                    if (index < filteredWordList.size) {
                        filteredWordList[index].documentId
                    } else {
                        "ad_$index"
                    }}) { index ->
                    val positionInBlock = index % (wordsPerAdBlock + adsPerBlock)
                    if (positionInBlock < wordsPerAdBlock) {
                        val wordIndex = index - index / (wordsPerAdBlock + adsPerBlock) * adsPerBlock
                        val word = filteredWordList[wordIndex]
                        WordCardItem(wordCard = word) {
                            viewModel.updateViewingWordCard(word)
                            navController.navigate("WordCardViewScreen")
                        }
                    } else {

                        AdMobBanner()
                    }
                }
            }
            AnimatedVisibility( modifier = Modifier.padding(horizontal = 30.dp, vertical = 0.dp),visible = textFieldVisible,) {
                OutlinedTextField(
                    shape = RoundedCornerShape(20.dp),
                    colors = customTextFieldColors,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onNext = null),
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    singleLine = true,
                    label = { Text("Search in your wordcards!") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        FloatingActionButton(
            backgroundColor = Color.Black,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(),
            onClick = {
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
fun SearchScreen(viewModel: CardViewModel, navController: NavHostController,state:LazyListState) {

    val wordList = viewModel.wordcardSearchstateFlow.collectAsStateWithLifecycle().value
    var searchQuery by remember { mutableStateOf("") }
    LaunchedEffect(key1 = searchQuery) {
   if(searchQuery.length>2) {viewModel.searchWordCardOnline(searchQuery)}
}
    val customTextFieldColors = TextFieldDefaults.textFieldColors(
        textColor = Color.Black,
        backgroundColor = Color.Transparent,
        cursorColor = Color.Black,
        focusedIndicatorColor = Color.Blue,
        unfocusedIndicatorColor = Color.Black,
        disabledIndicatorColor = Color.Transparent
    )
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
            OutlinedTextField(colors = customTextFieldColors, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
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
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {

            AdMobBanner()
        }
    }
}

@Composable
fun StudyCardsScreen(navigateStudy:()->Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                15.dp
            ),
        contentAlignment = Alignment.Center
    )

    {
        Image(painter = painterResource(id = R.drawable.work), contentDescription = "")
       Button(modifier = Modifier.align(Alignment.BottomCenter), onClick = navigateStudy){
           Text(text = "Lets Study")

       }
    }
}

@Composable
fun SettingsScreen(viewModel:CardViewModel) {
    val context= LocalContext.current

    var saveEnabled  by rememberSaveable {
        mutableStateOf(false)
    }

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    var expandedNumbers by rememberSaveable {
        mutableStateOf(false)
    }
    var settings by rememberSaveable {
        mutableStateOf(viewModel.getSettings(context = context))
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
      Icon(modifier = Modifier.align(Alignment.TopCenter), painter = painterResource(id = R.drawable.notify), contentDescription ="" )

Box(modifier =Modifier.align(Alignment.BottomCenter)){

    AdMobBanner()
}

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.3f), RoundedCornerShape(10.dp)
                    )
                    .animateContentSize(animationSpec = tween(500))
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = "Set up your WordCard reminder.", fontWeight = FontWeight.Bold)
                    Switch(checked = settings.reminderMode, onCheckedChange = {
                        settings = settings.copy(reminderMode = it)
                        saveEnabled = true

                    })
                }
                if (settings.reminderMode) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Your reminder will be shown every  ")

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = settings.repeatinterval.toString(),
                                modifier = Modifier.clickable { expandedNumbers = true }
                            )
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
                            DropdownMenu(
                                scrollState = ScrollState(settings.repeatinterval.toInt()),
                                expanded = expandedNumbers,
                                onDismissRequest = { expandedNumbers = false },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                for (a in 1..100) {
                                    DropdownMenuItem(onClick = {
                                        settings = settings.copy(repeatinterval = a.toLong())
                                        expandedNumbers = false
                                        saveEnabled = true
                                    }) {
                                        Text(a.toString())
                                    }
                                }
                            }
                        }
                        // DropdownMenu
                        Box(modifier = Modifier.padding(start = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Text(
                                    text = when (settings.timeUnit) {
                                        TimeUnit.DAYS -> "Days"
                                        TimeUnit.HOURS -> "Hours"
                                        TimeUnit.MINUTES -> "Minutes"
                                        else -> ""
                                    },
                                    modifier = Modifier.clickable { expanded = true }
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = ""
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    DropdownMenuItem(onClick = {
                                        settings = settings.copy(timeUnit = TimeUnit.MINUTES)
                                        expanded = false
                                        saveEnabled = true
                                    }) {
                                        Text("Minutes")
                                    }

                                    DropdownMenuItem(onClick = {
                                        settings = settings.copy(timeUnit = TimeUnit.HOURS)
                                        expanded = false
                                        saveEnabled = true
                                    }) {
                                        Text("Hours")
                                    }

                                    DropdownMenuItem(onClick = {
                                        settings = settings.copy(timeUnit = TimeUnit.DAYS)
                                        expanded = false
                                        saveEnabled = true
                                    }) {
                                        Text("Days")
                                    }
                                }
                            }
                        }
                    }
                }

                Button(enabled = saveEnabled, onClick = {

                    viewModel.uploadSettings(context = context, settings = settings)
                    if (settings.reminderMode) {
                        Toast.makeText(
                            context,
                            "Your reminder was set successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        val reminderWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                            settings.repeatinterval, settings.timeUnit
                        )

                            .setInitialDelay(settings.repeatinterval, settings.timeUnit)
                            .build()

                        val workManager = WorkManager.getInstance(context)
                        workManager.enqueueUniquePeriodicWork(
                            "reminderWork",
                            ExistingPeriodicWorkPolicy.REPLACE,
                            reminderWorkRequest
                        )


                    } else {
                        WorkManager.getInstance(context).cancelAllWork()
                        Toast.makeText(
                            context,
                            "Your reminder has been closed successfully",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }) {
                    Text(text = "Save your changes!")
                }
            }





        }
    }




@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BottomNav(viewModel: CardViewModel,navigateStudy:()->Unit) {
    val navController = rememberNavController()
  val bottomBarVisibility = remember {
      mutableStateOf(true)
  }
    Scaffold(modifier=Modifier,backgroundColor = Color.Transparent,

        bottomBar = {

AnimatedVisibility(visible = bottomBarVisibility.value) {
    BottomNavigation(modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        ,backgroundColor = hilalsColor, elevation = 20.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.arguments?.getString("androidx.navigation.dynamicfeatures.FragmentNavigator.Destination")
        var clictedItem by rememberSaveable {
            mutableIntStateOf(1)
        }



        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },

            selected = currentRoute == BottomNavScreens.Home.route,
            label = { if(clictedItem==1){
                Text(overflow = TextOverflow.Visible, text = "____________")}else{
                Text(text = "  ") }},
            onClick = {
                clictedItem=1
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
            label = { if(clictedItem==2){
                Text(overflow = TextOverflow.Visible, text = "____________")}else{
                Text(text = "  ") }},
            selected = currentRoute == BottomNavScreens.Search.route,
            onClick = {


                clictedItem=2
                navController.navigate(BottomNavScreens.Search.route) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    restoreState = true
                }
                bottomBarVisibility.value=true

            }
        )

        // Notifications Tab
        BottomNavigationItem(
            icon = { Icon(painterResource(R.drawable.reading), contentDescription = null) },
            label = { if(clictedItem==3){
                Text(overflow = TextOverflow.Visible, text = "____________")}else{
                Text(text = "  ") }},
            selected = currentRoute == BottomNavScreens.Study.route,
            onClick = {
                clictedItem=3
                navController.navigate(BottomNavScreens.Study.route) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    restoreState = true
                }
                bottomBarVisibility.value=true

            }
        )

        // Settings Tab
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { if(clictedItem==4){
                Text(overflow = TextOverflow.Visible, text = "____________")}else{
                Text(text = "  ") }},
            selected = currentRoute == BottomNavScreens.Settings.route,
            onClick = {
                clictedItem=4
                navController.navigate(BottomNavScreens.Settings.route) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    restoreState = true
                }
                bottomBarVisibility.value=true

            }
        )
    }
}

        }
    ) { innerPadding ->
        val lazyListState = rememberLazyGridState()
        val lazyListSearchState = rememberLazyListState()
        NavHost(
            navController = navController,
            startDestination = BottomNavScreens.Home.route,
            modifier = Modifier.padding(innerPadding)

        ) {


            composable(BottomNavScreens.Home.route) {

                HomeScreen(viewModel, navController, lazyListState,bottomBarVisibility)
            }
            composable(BottomNavScreens.Search.route) { SearchScreen(viewModel,navController,lazyListSearchState) }
            composable(BottomNavScreens.Study.route) { StudyCardsScreen(navigateStudy) }
            composable(BottomNavScreens.Settings.route) { SettingsScreen(viewModel) }
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
                    saveClick = {  viewModel.viewingWorCard.value?.let {word->viewModel.viewModelScope.launch { viewModel.saveWordCard(word,false)}}})

            }

        }
    }


}


