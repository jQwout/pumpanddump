package ai.bump_dump


import ai.bump_dump.assets.ui.uiThemeColors
import ai.bump_dump.settings.domain.SignalSettingsRepository
import ai.bump_dump.screener.ui.SignalListScreen
import ai.bump_dump.shared.ServiceLocator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


fun main() = application {
    val presenter = ServiceLocator.mainPresenter
    Window(
        onCloseRequest = ::exitApplication,
        title = "Dump And Bump",
        alwaysOnTop = true,
    ) {
        val width = 560.dp
        val minHeight = 600.dp

        window.minimumSize = java.awt.Dimension(width.value.toInt(), minHeight.value.toInt())
        window.maximumSize = java.awt.Dimension(width.value.toInt(), Int.MAX_VALUE)

        presenter.isDarkTheme?.let { colors ->
            MaterialTheme(colors = uiThemeColors(colors)) {
                SignalListScreen(
                    ServiceLocator.signalListPresenter
                )
            }
        }
    }
}


class MainPresenter(
    private val coroutineScope: CoroutineScope,
    private val settingsRepository: SignalSettingsRepository
) {
    var isDarkTheme: Boolean? by mutableStateOf(true)
        private set

    init {
        coroutineScope.launch {
            settingsRepository.load()
        }

        coroutineScope.launch {
            settingsRepository.signalSettings.filterNotNull().collect {
                //isDarkTheme = it.isDarkTheme
            }
        }
    }
}