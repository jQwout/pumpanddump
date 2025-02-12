package domain.signals

import domain.tickers.TickersService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface SignalService {

    fun provide(period: Long = 10_000): Flow<List<SignalData>>

    class Impl(
        private val tickersService: TickersService,
        private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO // код этого класса должен выполняться
        // на фоновом потоке
        // по хорошему, нужно написать аналогичный SignalStorage как TickersStorage
    ) : SignalService {

        override fun provide(period: Long): Flow<List<SignalData>> {
            return flow {
                var lastSignals: List<SignalData> = emptyList()

                while (true) {
                    val tickets = tickersService.get()
                    println("new tickets : ${tickets.map.size}")
                    val signals = signalMapper(tickets.map).orEmpty()
                    println("new signals : ${signals.size}")

                    // Фильтруем только новые сигналы
                    val newSignals = signals.filter { newSignal ->
                        lastSignals.none { oldSignal -> isDuplicateSignal(newSignal, oldSignal) }
                    }

                    if (newSignals.isNotEmpty()) {
                        emit(newSignals)
                    }

                    println("checked signals : ${newSignals.size}")

                    lastSignals = signals // Обновляем список старых сигналов
                    delay(period)
                }
            }
                .flowOn(dispatcherIO)
        }


        private fun isDuplicateSignal(newSignal: SignalData, existingSignals: SignalData): Boolean {
            // Проверка на дубликат по дате или по тикеру и изменению цены
            val isDuplicate = existingSignals.date == newSignal.date ||
                    (existingSignals.tickerName == newSignal.tickerName &&
                            existingSignals.priceChange == newSignal.priceChange)

            // Проверка на соответствие условиям по изменению цены
           // val isPriceChangeValid = newSignal.priceChange.m1 > existingSignals.priceChange.m1 ||
           //         newSignal.priceChange.m5 > existingSignals.priceChange.m5 ||
           //         newSignal.priceChange.m10 > existingSignals.priceChange.m10

            // Возвращаем true только если сигнал является дубликатом и изменение цены соответствует условиям
            return isDuplicate
                    //&& (isPriceChangeValid
        }

    }
}