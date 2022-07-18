package com.rhounsaweatherapp.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rhounsaweatherapp.R.*
import com.rhounsaweatherapp.repository.RefreshState
import com.rhounsaweatherapp.repository.USER_LOCATION_CITY_ID
import com.rhounsaweatherapp.ui.theme.Violet
import com.rhounsaweatherapp.ui.theme.VioletDegrade
import com.rhounsaweatherapp.ui.viewModels.ForecastViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Created by rhounsa on 18/07/2022.
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val HIDE_LOCATION_RATIONALE = booleanPreferencesKey("hide_rationale")


@SuppressLint("FlowOperatorInvokedInComposition")
@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun Forecast(
    vm: ForecastViewModel,
    navController: NavController,
    currentListState: LazyListState,
    hourlyListState: LazyListState,
    dailyListState: LazyListState,
    pagerState: PagerState,
    scaffoldState: ScaffoldState,
) {
    val refreshState = vm.state.collectAsState()
    val currentForecast = vm.currentForecast.collectAsState()
    val hourlyForecast = vm.hourlyForecast.collectAsState()
    val dailyForecast = vm.dailyForecast.collectAsState()
    val alerts = vm.alerts.collectAsState()
    val cities = vm.cities.collectAsState()
    val currentCityName = vm.currentCityName.collectAsState("")
    val context = LocalContext.current
    val hideRationale =
        context.dataStore.data.map { it[HIDE_LOCATION_RATIONALE] ?: false }.collectAsState(true)

    val scope = rememberCoroutineScope()

    val (errorShown, setErrorShown) = rememberSaveable { mutableStateOf(false) }

    val (showCurrent, setShowCurrent) = rememberSaveable { mutableStateOf(true) }
    val (showHourly, setShowHourly) = rememberSaveable { mutableStateOf(false) }
    val (showDaily, setShowDaily) = rememberSaveable { mutableStateOf(false) }
    val (currentUpdate, setCurrentUpdate) = rememberSaveable { mutableStateOf("") }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ForecastAppBar(
                pagerState,
                currentCityName.value,
                {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                {
                    scope.launch {
                        vm.removeCurrentCity()
                        scaffoldState.snackbarHostState.showSnackbar(context.getString(string.location_removed_message))
                    }
                },
                {
                    setShowCurrent(true)
                    setShowHourly(false)
                    setShowDaily(false)
                },
                {
                    setShowCurrent(false)
                    setShowHourly(true)
                    setShowDaily(false)
                },
                {
                    setShowCurrent(false)
                    setShowHourly(false)
                    setShowDaily(true)
                },
//                { showSourceCode(context) }
            )
        },
        drawerContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(MaterialTheme.colors.background)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary,
                                MaterialTheme.colors.primaryVariant
                            )
                        )
                    )
            ) {
                item {
                    Image(
                        painter = painterResource(drawable.headermenu),
                        contentDescription = stringResource(string.snow),

                        )
                }
                item {
                    DrawerItem(
                        text = stringResource(string.your_location),
                        selected = currentCityName.value.isEmpty()
                    ) {
                        vm.setCurrentCity(USER_LOCATION_CITY_ID)
                        scope.launch { scaffoldState.drawerState.close() }
                    }
                }
                items(cities.value) { city ->
                    DrawerItem(text = city.name, selected = city.selected) {
                        vm.setCurrentCity(city.id)
                        scope.launch { scaffoldState.drawerState.close() }
                    }
                }
                item {
                    DrawerItem(text = stringResource(string.add_location), selected = false) {
                        navController.navigate(MainDestinations.CITY_SELECT_ROUTE)
                    }
                }
            }
        },
        content = {
            SwipeRefresh(
                state = rememberSwipeRefreshState(refreshState.value == RefreshState.Loading),
                onRefresh = {
                    setErrorShown(false)
                    vm.refresh()
                },
                modifier = Modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Violet,
                            VioletDegrade
                        )
                    )
                ),
            ) {
                if (currentCityName.value.isEmpty() && refreshState.value == RefreshState.PermissionError) {
                    LocationRequestScreen(
                        { navController.navigate(MainDestinations.CITY_SELECT_ROUTE) },
                        { showAppSettingsPage(context) },
                        { setLocationRationaleHidden(scope, context) },
                        vm::onPermissionGranted,
                        hideRationale.value
                    )
                } else if (refreshState.value == RefreshState.Loading && currentForecast.value == null) {
                    EmptyLoadingPage()
                } else {

                    if (showCurrent){
                        CurrentForecast( currentListState, currentForecast.value, alerts.value)
                    }
                    if (showHourly){
                        HourlyForecast(hourlyListState, hourlyForecast.value)
                    }
                    if (showDaily){
                        DailyForecast(dailyListState, dailyForecast.value)
                    }

                }
            }
        }
    )
    if (!errorShown) {
        setErrorShown(showErrorMessage(refreshState.value, scope, scaffoldState, context))
    }
}

fun showErrorMessage(
    refreshState: RefreshState,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    context: Context
): Boolean {
    var shown = false
    (refreshState as? RefreshState.Error)?.let { error ->
        scaffoldState.snackbarHostState.run {
            // Prevent duplicate snackbars when loading multiple cities
            val message = context.getString(error.message)
            if (currentSnackbarData?.message != message) {
                scope.launch { showSnackbar(message) }
                shown = true
            }
        }
    }
    return shown
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun EmptyLoadingPage() {
    LazyColumn(content = {}, modifier = Modifier.fillMaxSize())
}


@ExperimentalPagerApi
@Composable
fun ForecastAppBar(
    pagerState: PagerState,
    currentCityName: String,
    openDrawer: () -> Unit,
    removeLocation: () -> Unit,
    showCurrent: (Boolean) -> Unit,
    showHourly: (Boolean) -> Unit,
    showDaily: (Boolean) -> Unit,
//    showSource: () -> Unit
) {
    val (showMenu, setShowMenu) = rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val tabTitles = stringArrayResource(array.tab_titles)

    Column {
        Row (verticalAlignment = Alignment.CenterVertically){

            IconButton(onClick = { openDrawer() }) {
                Image(
                    painter = painterResource(drawable.menu),
                    contentDescription = stringResource(string.snow),
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),

                    )
            }
            Column(modifier = Modifier.fillMaxWidth(0.6f)) {

                Text(if (currentCityName.isEmpty()) stringResource(string.your_location) else currentCityName, style = MaterialTheme.typography.h6)
                Text(if (currentCityName.isEmpty()) stringResource(string.empty) else currentCityName, style = MaterialTheme.typography.subtitle2)

            }

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showCurrent(true) }) {
                    Image(
                        painter = painterResource(drawable.current),
                        contentDescription = stringResource(string.current),
                        colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .width(25.dp)


                    )
                }
                IconButton(onClick = { showHourly(true) }) {
                    Image(
                        painter = painterResource(drawable.hourly),
                        contentDescription = stringResource(string.hourly),
                        colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .width(25.dp)

                    )
                }
                IconButton(onClick = { showDaily(true) }) {
                    Image(
                        painter = painterResource(drawable.daily),
                        contentDescription = stringResource(string.daily),
                        colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .width(25.dp)

                    )
                }
            }

        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun LocationRequestScreen(
    navigateToSearchScreen: () -> Unit,
    navigateToSettingsScreen: () -> Unit,
    hideRationale: () -> Unit,
    onPermissionGranted: () -> Unit,
    doNotShowRationaleInit: Boolean
) {
    val (doNotShowRationale, setDoNotShowRationale) = rememberSaveable {
        mutableStateOf(
            doNotShowRationaleInit
        )
    }

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)

    when {
        permissionState.hasPermission -> {
            onPermissionGranted()
            EmptyLoadingPage()
        }
        permissionState.shouldShowRationale || !permissionState.permissionRequested -> {
            if (doNotShowRationale) {
                hideRationale()
                PermissionDenied(navigateToSettingsScreen, navigateToSearchScreen)
            } else {
                AskPermission(
                    permissionState,
                    { setDoNotShowRationale(true) },
                    navigateToSearchScreen
                )
            }
        }
        else -> {
            hideRationale()
            PermissionDenied(navigateToSettingsScreen, navigateToSearchScreen)
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun AskPermission(
    permissionState: PermissionState,
    setDoNotShowRationale: () -> Unit,
    navigateToSearchScreen: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            stringResource(string.location_permission_request),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text(stringResource(string.yes))
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = setDoNotShowRationale) {
                Text(stringResource(string.no_thanks), textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(string.manual_location_prompt))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = navigateToSearchScreen) {
            Text(stringResource(string.search))
        }
    }
}

@Composable
fun PermissionDenied(navigateToSettingsScreen: () -> Unit, navigateToSearchScreen: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            stringResource(string.location_permission_denied),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = navigateToSettingsScreen) {
            Text(stringResource(string.open_settings_prompt))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(string.manual_location_prompt))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = navigateToSearchScreen) {
            Text(stringResource(string.search))
        }
    }
}

fun showAppSettingsPage(context: Context) {
    ContextCompat.startActivity(
        context,
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context.packageName, null)
        },
        null
    )
}

fun setLocationRationaleHidden(scope: CoroutineScope, context: Context) {
    scope.launch {
        context.dataStore.edit {
            it[HIDE_LOCATION_RATIONALE] = true
        }
    }
}

//fun showSourceCode(context: Context) {
//    ContextCompat.startActivity(
//        context,
//        Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(string.source_code_url))),
//        null
//    )
//}

@Composable
fun DrawerItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .background(if (selected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}