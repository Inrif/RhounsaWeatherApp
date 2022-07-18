package com.rhounsaweatherapp.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rhounsaweatherapp.R

/**
 * Created by rhounsa on 18/07/2022.
 */

// Set of Material typography styles to start with

var fonts = FontFamily(
    Font(R.font.neusa_bold, weight = FontWeight.Bold),
    Font(R.font.neusa_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.neusa_regular, weight = FontWeight.Normal),
    Font(R.font.neusa_medium, weight = FontWeight.Medium),
    Font(R.font.neusa_light, weight = FontWeight.Light),
    Font(R.font.neusa_thin, weight = FontWeight.Thin),
)
// Set of Material typography styles to start with
val Typography = Typography(

    subtitle1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.15.sp,
        color = VioletDegrade
    ),
    body1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = fonts,
        fontSize = 12.sp,
        color = VioletDegrade
    ),
    subtitle2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        color = White
    ),
    h6 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = White
    )

)