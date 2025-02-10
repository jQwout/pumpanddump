package ai.bump_dump.shared

import ai.bump_dump.MainPresenter
import ai.bump_dump.screener.ui.SignalListPresenter
import ai.bump_dump.settings.domain.SignalSettingsRepository
import domain.signals.SignalService
import domain.tickers.TickersService
import domain.tickers.TickersStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File

object ServiceLocator {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val tickersStorage: TickersStorage by lazy {
        TickersStorage.Ram
    }

    private val tickersService: TickersService by lazy {
        TickersService(scope, tickersStorage)
    }

    private val signalService: SignalService by lazy {
        SignalService.Impl(tickersService)
    }

    private val signalSettingsRepository: SignalSettingsRepository by lazy {
        SignalSettingsRepository(File("settings.json"), scope)
    }

    val signalListPresenter: SignalListPresenter by lazy {
        SignalListPresenter(signalService, signalSettingsRepository, scope)
    }

    val mainPresenter by lazy {
        MainPresenter(scope, signalSettingsRepository)
    }
}