package ai.bump_dump.window

import ai.bump_dump.settings.domain.SignalSettingsRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class WindowPresenter(
    private val coroutineScope: CoroutineScope,
    private val settingsRepository: SignalSettingsRepository
) {
    var isDarkTheme: Boolean? by mutableStateOf(true)
        private set

    init {
        coroutineScope.launch {
            settingsRepository.signalSettings.filterNotNull().collect {
                isDarkTheme = it.isDarkTheme
            }
        }
    }
}