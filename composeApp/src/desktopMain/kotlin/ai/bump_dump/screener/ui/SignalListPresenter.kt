package ai.bump_dump.screener.ui

import ai.bump_dump.settings.domain.SignalSettingsDto
import ai.bump_dump.settings.domain.SignalSettingsRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import domain.signals.SignalData
import domain.signals.SignalService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SignalListPresenter(
    private val signalService: SignalService,
    private val signalSettingsRepository: SignalSettingsRepository,
    private val coroutineScope: CoroutineScope
) {
    var signals by mutableStateOf<List<SignalData>>(emptyList())
        private set

    var showDialog by mutableStateOf(false)
        private set

    var settings: SignalSettingsDto? by mutableStateOf(null)
        private set

    init {
        loadSettings()
        observeSignals()
    }

    fun toggleDialog() {
        showDialog = !showDialog
    }

    fun saveSettings(newSettings: SignalSettingsDto) {
        coroutineScope.launch {
            signalSettingsRepository.save(newSettings)
            settings = signalSettingsRepository.load()
            onNewSettings()
        }
    }

    private fun onNewSettings() {
        signals = emptyList()
    }

    private fun loadSettings() {
        coroutineScope.launch {
            settings = signalSettingsRepository.load()
        }
    }

    private fun observeSignals() {
        coroutineScope.launch {
            signalService.provide(period = 10_000)
                .map(::filterByUnique)
                .map(::filterBySettings)
                .collect { newSignals ->
                    signals += newSignals
                }
        }
    }

    private fun filterBySettings(newSignals: List<SignalData>): List<SignalData> {
        val filtered = settings?.let { settings ->
            newSignals.filter { signal ->
                val priceInRange = signal.price.toFloat() in settings.priceRange
                val volumeInRange = signal.volume.toFloat() in settings.volumeRange
                val ratingInRange = signal.placeOnStock.toFloat() in settings.ratingRange

                priceInRange && volumeInRange && ratingInRange
            }
        } ?: newSignals

        return filtered
    }

    private fun filterByUnique(newSignals: List<SignalData>): List<SignalData> {
        val unique = newSignals.filter { newSign ->
            signals.none { newSign.key == it.key }
        }
        return unique
    }
}