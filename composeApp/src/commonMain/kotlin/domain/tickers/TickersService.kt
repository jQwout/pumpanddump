package domain.tickers

import data.tickers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TickersService(
    private val coroutineScope: CoroutineScope,
    private val tickersStorage: TickersStorage,
    private val pollingMs: Long = 50_000
) {

    init {
        coroutineScope.launch {
            try {
                while (true) {
                    val data = tickers()
                    val map = mapToTickersMap(data.data)
                    tickersStorage.put(map)
                    delay(pollingMs)
                }
            } catch (e: Throwable) {
                // later
            }
        }
    }

    private fun mapToTickersMap(list: List<TickerData>) = list.associateBy { it.symbol }

    suspend fun get() = tickersStorage.get()
}
