package ai.bump_dump


import ai.bump_dump.window.WindowController
import androidx.compose.ui.window.application


fun main() = application {
    WindowController(::exitApplication)
}
