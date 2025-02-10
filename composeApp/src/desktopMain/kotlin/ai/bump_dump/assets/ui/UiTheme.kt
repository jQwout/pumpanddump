package ai.bump_dump.assets.ui

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors

fun uiThemeColors(isDark: Boolean)  =  if (isDark) {
    darkColors()
} else {
    lightColors()
}