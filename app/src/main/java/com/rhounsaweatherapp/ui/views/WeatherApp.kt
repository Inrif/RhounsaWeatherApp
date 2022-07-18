package com.rhounsaweatherapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.rhounsaweatherapp.ui.theme.Theme
import com.rhounsaweatherapp.ui.views.WeatherNavGraph
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by rhounsa on 18/07/2022.
 */

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun WeatherApp () {
    val currentListState = rememberLazyListState()
    val hourlyListState = rememberLazyListState()
    val dailyListState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = 3)
    val scaffoldState = rememberScaffoldState()

    Theme {
        WeatherNavGraph(
            currentListState = currentListState,
            hourlyListState = hourlyListState,
            dailyListState = dailyListState,
            pagerState = pagerState,
            scaffoldState = scaffoldState
        )
    }
}