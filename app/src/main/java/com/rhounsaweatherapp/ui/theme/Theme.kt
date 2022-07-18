package com.rhounsaweatherapp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Created by rhounsa on 18/07/2022.
 */
private val DarkColorPalette = darkColors(
    primary = Violet,
    primaryVariant = VioletDegrade,
    secondary = Violet,
    error = Red300,
    background = Violet,
    surface = White,
    onPrimary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
    onSecondary = Color.White
)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Violet,
    primaryVariant = VioletDegrade,
    secondary = Violet,
    error = Red200,
    background = Violet,
    surface = White,
    onPrimary = White,
    onSurface = Color.Black,
    onBackground = Color.Black,
    onSecondary = Color.White
)


val Colors.hourlyBase: Color
    @Composable get() = if (isLight) LightBlue100 else Indigo500
val Colors.hourlyOverlay: Color
    @Composable get() = if (isLight) LightBlue200 else Indigo900


@Composable
fun Theme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}