package com.rhounsaweatherapp.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.rhounsaweatherapp.R.*
import com.rhounsaweatherapp.model.weather.Alert
import com.rhounsaweatherapp.model.weather.Current
import com.rhounsaweatherapp.ui.theme.Violet
import com.rhounsaweatherapp.ui.theme.White
import com.rhounsaweatherapp.ui.theme.WhiteWithOpacity
import com.rhounsaweatherapp.utils.TimeFormat

import java.time.ZonedDateTime

/**
 * Created by rhounsa on 18/07/2022.
 */


private val gridPadding = 24.dp

@Composable
fun CurrentForecast(listState: LazyListState, current: Current?, alerts: List<Alert>) {
    if (current == null) {
        NoDataMessage(message = stringResource(string.no_data_message_current))
        return
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        current.run {
            item {
                Weather(
                    description,
                    icon,
                    temp,
                    feelsLike,
                    uvi,
                    lastUpdated
                )

            }
            item {

                Column(
//

                ) {


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WhiteWithOpacity, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        GridColumn {
                            Wind(
                                windSpeed,
                                windGust,
                                windDirection
                            )
                            Cloudiness(clouds)
                        }
                        GridColumn {
                            Humidity(humidity)
                            Sunrise(sunrise)

                        }
                        GridColumn {
                            Pressure(pressure)
                            Sunset(sunset)

                        }
                        GridColumn {
                            Visibility(visibility)
                            DewPoint(dewPoint)
                        }

                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 0.dp, vertical = 18.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {

                        val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
                        val (showFullDialog, setShowFullDialog) = remember { mutableStateOf(false) }


                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { setShowDialog(true) }
                        ) {
                            Text("Alerte")
                        }

                        // Create alert dialog, pass the showDialog state to this Composable
                        DialogDemo(showDialog, setShowDialog, alerts)
                    }
                }


            }
        }
    }
}

@Composable
fun DialogDemo(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, alerts: List<Alert>) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },
//            title = {
//                Text("Alert")
//            },
            confirmButton = {

            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = White,
                        contentColor = Violet
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    onClick = {
                        // Change the state to close the dialog
                        setShowDialog(false)
                    },

                    ) {
                    Text("Fermer")
                }
            },
            text = {
                LazyColumn(
//                    state = listState,
                    contentPadding = PaddingValues(1.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    items(
                        items = alerts
                    ) { alert ->
                        Alert(alert)
                    }
                }
            },
        )
    }
}

@Composable
fun FullScreenDialog(showDialog: Boolean, setShowDialog: (Boolean) -> Unit) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {
            },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = White
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.TopCenter),
                        text = "top"
                    )
                    Text("center")
                    Text(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        text = "bottom"
                    )
                }
            }
        }
    }
}

@Composable
fun TimeUpdated(time: ZonedDateTime) {
    Text(
        text = stringResource(
            string.time_updated,
            TimeFormat.toDate(time)
        ),
        style = MaterialTheme.typography.subtitle2,
    )
}

@Composable
fun Weather(description: String, icon: String, temp: Int, feelsLike: Int, uvi: Int, lastUpdated:ZonedDateTime) {

    Column(
        modifier = Modifier.fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = rememberImagePainter(
                stringResource(
                    string.icon_url,
                    icon
                )
            ),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(string.degrees_unit, temp),
                style = MaterialTheme.typography.h2,
                fontWeight = FontWeight.Bold
            )
            Text(text = stringResource(string.feels_like, feelsLike))
            Text(description)
            Text(stringResource(string.uv_index, uvi))
            Spacer(modifier = Modifier.height(20.dp))
            TimeUpdated(lastUpdated)
        }

    }

}

@Composable
fun GridColumn(content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        content()
    }
}

@Composable
fun GridItem(padded: Boolean = false, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = if (padded) gridPadding else 0.dp)
    ) {
        content()
    }
}

@Composable
fun Wind(speed: Int, gust: Int, direction: String) {
    GridItem {
        Image(
            painter = painterResource(drawable.wind),
            contentDescription = null
        )
        Text(
            text = stringResource(string.wind),
            style = MaterialTheme.typography.subtitle1

        )
        Text(
            text = stringResource(string.wind_speed, speed, direction),
            style = MaterialTheme.typography.body2
        )
        Text(
            text = stringResource(string.wind_speed_gust, gust),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun Cloudiness(clouds: Int) {
    GridItem {
        Image(
            painter = painterResource(drawable.cloud),
            contentDescription = null
        )
        Text(
            text = stringResource(string.cloudiness),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = stringResource(string.int_percent, clouds),
            style = MaterialTheme.typography.body2

        )
    }
}

@Composable
fun Visibility(visibility: Int) {
    GridItem(padded = true) {

        Image(
            painter = painterResource(drawable.telescope),
            contentDescription = null
        )
        Text(
            text = stringResource(string.visibility),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = stringResource(string.kilometers, visibility),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun Pressure(pressure: Double) {
    GridItem(padded = true) {
        Image(
            painter = painterResource(drawable.barometer),
            contentDescription = null
        )
        Text(
            text = stringResource(string.pressure),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            stringResource(string.kpa, "%.1f".format(pressure)),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun Humidity(humidity: Int) {
    GridItem(padded = true) {
        Image(
            painter = painterResource(drawable.humidity),
            contentDescription = null
        )
        Text(
            text = stringResource(string.humidity),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            stringResource(string.int_percent, humidity),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun DewPoint(dewPoint: Int) {
    GridItem {
        Image(
            painter = painterResource(drawable.dewpoint),
            contentDescription = null
        )
        Text(
            text = stringResource(string.dew_point),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            stringResource(string.degrees_unit, dewPoint),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun Sunrise(sunrise: ZonedDateTime) {
    GridItem {
        Image(
            painter = painterResource(drawable.sunrise),
            contentDescription = null
        )
        Text(
            text = stringResource(string.sunrise),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = TimeFormat.toDayHour(sunrise),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun Sunset(sunset: ZonedDateTime) {
    GridItem {
        Image(
            painter = painterResource(drawable.sunset),
            contentDescription = null
        )
        Text(
            text = stringResource(string.sunset),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = TimeFormat.toDayHour(sunset),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun Alert(alert: Alert) {
    val caption = MaterialTheme.typography.caption
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = alert.event, style = MaterialTheme.typography.subtitle1)
        Text(
            text = stringResource(
                string.alert_start_time,
                TimeFormat.toDate(alert.start)
            ), style = caption
        )
        Text(
            text = stringResource(
                string.alert_end_time,
                TimeFormat.toDate(alert.end)
            ), style = caption
        )
        Row {
            Text(
                text = stringResource(string.alert_issued_by),
                style = caption
            )
            Text(text = alert.senderName, fontWeight = FontWeight.SemiBold, style = caption)
        }
        Divider(
            color = MaterialTheme.colors.onSecondary,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(text = alert.description, style = MaterialTheme.typography.body2)
    }
}