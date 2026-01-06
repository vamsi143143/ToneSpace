package com.example.tonespace.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ToneSpaceColorScheme = lightColorScheme(
    primary = PrimaryBrown,
    background = BeigeLight,
    surface = BeigeLight,
    onPrimary = TextLight,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun ToneSpaceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ToneSpaceColorScheme,
        typography = Typography,
        content = content
    )
}
