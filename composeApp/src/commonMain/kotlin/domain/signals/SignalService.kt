package domain.signals

import domain.tickers.TickersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface SignalService {

    fun provide(period: Long): Flow<List<SignalData>>

    class Impl(
        private val tickersService: TickersService,
    ) : SignalService {

        override fun provide(period: Long): Flow<List<SignalData>> {
            return flow {
                var lastSignals: List<SignalData> = emptyList()

                while (true) {
                    val tickets = tickersService.get()
                    val signals = signalMapper(tickets).orEmpty()

                    // Фильтруем только новые сигналы
                    val newSignals = signals.filter { newSignal ->
                        lastSignals.none { oldSignal -> isDuplicateSignal(newSignal, oldSignal) }
                    }

                    if (newSignals.isNotEmpty()) {
                        emit(newSignals)
                    }

                    lastSignals = signals // Обновляем список старых сигналов
                    delay(period)
                }
            }
                .flowOn(Dispatchers.IO)
        }


        private fun isDuplicateSignal(newSignal: SignalData, existingSignals: SignalData): Boolean {

            return existingSignals.date == newSignal.date
                    || (existingSignals.tickerName == newSignal.tickerName && existingSignals.priceChange == newSignal.priceChange)
        }

    }
}