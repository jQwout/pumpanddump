package ai.bump_dump.screener.ui

import ai.bump_dump.settings.domain.SignalSettingsDto
import ai.bump_dump.settings.domain.SignalSettingsRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import domain.signals.SignalData
import domain.signals.SignalService
import domain.signals.m2
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
            println("filterBySettings :$newSignals")
            newSignals.filter { signal ->
                val ratingInRange = signal.placeOnStock.toFloat() in settings.ratingMin..settings.ratingMax

                val bumpM1 = signal.priceChange.m1 >= (settings.bumpSize)
                val bumpM2 = signal.priceChange.m2() >= (settings.bumpSize * 1.2)
                val bumpM3 = signal.priceChange.m3 >= (settings.bumpSize * 1.4)
                val bumpM5 = signal.priceChange.m5 >= (settings.bumpSize * 2)

                val dumpM1 = (signal.priceChange.m1 * -1) >= settings.dumpSize
                val dumpM3 = (signal.priceChange.m3 * -1) >= settings.dumpSize * 1.4
                val dumpM5 = (signal.priceChange.m5 * -1) >= settings.dumpSize * 1.5

                val y = ratingInRange
                        && (bumpM1 || bumpM2 || bumpM3 || bumpM5 )
                        && (dumpM1  || dumpM3 || dumpM5)
                y
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