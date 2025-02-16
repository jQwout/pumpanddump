package ai.bump_dump.settings.ui

import ai.bump_dump.settings.domain.SignalSettingsDto
import ai.bump_dump.settings.domain.SignalSettingsRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SignalListSettingsPresenter(
    private val signalSettingsRepository: SignalSettingsRepository,
    private val coroutineScope: CoroutineScope
) {

    var settings: SignalSettingsDto? by mutableStateOf(null)
        private set

    init {
        loadSettings()
    }

    fun saveSettings(newSettings: SignalSettingsDto) {
        coroutineScope.launch {
            signalSettingsRepository.save(newSettings)
            settings = signalSettingsRepository.load()
        }
    }

    private fun loadSettings() {
        coroutineScope.launch {
            settings = signalSettingsRepository.load()
        }
    }
}