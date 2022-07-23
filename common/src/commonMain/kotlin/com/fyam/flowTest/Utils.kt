package com.fyam.flowTest

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.currentCoroutineContext

suspend fun currentCoroutineName() = currentCoroutineContext()[CoroutineName]?.name.orEmpty()

fun Typography.copy(
    defaultFontFamily: FontFamily = FontFamily.Default,
    h1: TextStyle = this.h1,
    h2: TextStyle = this.h2,
    h3: TextStyle = this.h3,
    h4: TextStyle = this.h4,
    h5: TextStyle = this.h5,
    h6: TextStyle = this.h6,
    subtitle1: TextStyle = this.subtitle1,
    subtitle2: TextStyle = this.subtitle2,
    body1: TextStyle = this.body1,
    body2: TextStyle = this.body2,
    button: TextStyle = this.button,
    caption: TextStyle = this.caption,
    overline: TextStyle = this.overline
): Typography = Typography(
    defaultFontFamily,
    h1 = h1,
    h2 = h2,
    h3 = h3,
    h4 = h4,
    h5 = h5,
    h6 = h6,
    subtitle1 = subtitle1,
    subtitle2 = subtitle2,
    body1 = body1,
    body2 = body2,
    button = button,
    caption = caption,
    overline = overline,
)
