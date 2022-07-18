package com.rhounsaweatherapp.ui.views

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rhounsaweatherapp.R.*
import com.rhounsaweatherapp.model.cities.Country
import com.rhounsaweatherapp.ui.theme.Violet
import com.rhounsaweatherapp.ui.theme.VioletDegrade
import com.rhounsaweatherapp.ui.theme.White
import com.rhounsaweatherapp.ui.viewModels.CityViewModel

/**
 * Created by rhounsa on 18/07/2022.
 */
@ExperimentalFoundationApi
@Composable
fun CitySelect(
    vm: CityViewModel,
    navController: NavController,
) {
    val allCities = vm.filteredCities.collectAsState()
    val countries = vm.countries.collectAsState()
    val query = vm.query.collectAsState()
    val selectedIndex = vm.countryIndex.collectAsState()

    val (expanded, setExpanded) = rememberSaveable { mutableStateOf(false) }

    if (expanded) {
        BackHandler(onBack = { setExpanded(false) })
        CountrySelector(
            countries.value,
            selectedIndex.value,
            vm::selectCountry,
            setExpanded
        )
        return
    }
    Scaffold(
        topBar = { CitySelectAppBar(navController::navigateUp) },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Violet,
                                VioletDegrade
                            )
                        )
                    ),
            ) {
                stickyHeader {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scaleY = 1F, scaleX = 01F)
                                .background(
                                    White,
                                    shape = RoundedCornerShape(30.dp),
                                )
                                .padding(0.dp)
                                .height(45.dp), // Here I have decreased the height
                        ) {
                            Text(text = countries.value[selectedIndex.value].name,)
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = stringResource(string.country_selector_icon_label),
                                modifier = Modifier
                                    .clickable(onClick = { setExpanded(true) })
                            )

                        }
                        TextField(
                            value = countries.value[selectedIndex.value].name,
                            textStyle = MaterialTheme.typography.subtitle1,
                            readOnly = false,
                            onValueChange = {},
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    contentDescription = stringResource(string.country_selector_icon_label),
                                    modifier = Modifier
                                        .clickable(onClick = { setExpanded(true) })
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scaleY = 1F, scaleX = 01F)
                                .background(
                                    White,
                                    shape = RoundedCornerShape(30.dp),
                                )
                                .padding(0.dp)
                                .height(45.dp), // Here I have decreased the height
                            shape = RoundedCornerShape(30.dp),
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        SearchBox(query.value, vm::setQuery)
                    }
                }
                items(
                    items = allCities.value
                ) { city ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (city.saved) {
                                    vm.removeCity(city.id)
                                } else {
                                    vm.addCity(city.id)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp))
                    {
                        Text(
                            text = city.name,
                            modifier = Modifier.weight(1f)
                        )
                        if (city.saved) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = stringResource(string.city_saved_icon_label),
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CitySelectAppBar(onBackPress: () -> Boolean) {
    Column {
        TopAppBar(
            title = {
                Text(stringResource(string.city_select_app_bar_title))
            },
            elevation = 0.dp,
            navigationIcon = {
                IconButton(onClick = { onBackPress() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(string.back_button_label))
                }
            }
        )
    }
}

@Composable
fun SearchBox(query: String, setQuery: (String) -> Unit) {
    val requester = FocusRequester()
    TextField(
        value = query,
        onValueChange = setQuery,
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = stringResource(string.clear_city_search_icon_label),
                    modifier = Modifier.clickable { setQuery("") }
                )
            }
        },
        placeholder = {
            Text(text = stringResource(string.city_search_placeholder))
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleY = 1F, scaleX = 01F)
            .focusRequester(requester)
            .background(
                White,
                shape = RoundedCornerShape(30.dp),
            )
            .padding(0.dp)
            .height(45.dp), // Here I have decreased the height
        shape = RoundedCornerShape(30.dp),
    )
    SideEffect {
        requester.requestFocus()
    }
}

@Composable
fun CountrySelector(
    countries: List<Country>,
    selectedIndex: Int,
    setSelectedIndex: (Int) -> Unit,
    setExpanded: (Boolean) -> Unit
) {
    val state = rememberLazyListState()
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items = countries) { i, country ->
            Text(
                text = country.name,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .background(
                        if (i == selectedIndex) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.background
                    )
                    .clickable {
                        setSelectedIndex(i)
                        setExpanded(false)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
    LaunchedEffect(key1 = 'a') {
        state.animateScrollToItem(selectedIndex)
    }
}

@Composable
fun BackHandler(onBack: () -> Unit) {
    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher ?: return
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnBack by rememberUpdatedState(onBack)
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = currentOnBack()
        }
    }
    backCallback.isEnabled = true

    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}
