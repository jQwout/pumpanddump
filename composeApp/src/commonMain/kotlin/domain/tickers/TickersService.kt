package domain.tickers


import data.fetchActiveFutures
import data.tickers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TickersService(
    private val coroutineScope: CoroutineScope,
    private val tickersStorage: TickersStorage,
    private val pollingMs: Long = 10_000
) {

    init {
        coroutineScope.launch {
            while (true) {
                try {
                    val data = tickers().fetchActiveFutures()
                    println("new data received: ${System.currentTimeMillis()}")
                    val map = mapToTickersMap(data)
                    println("new map received: ${System.currentTimeMillis()}")
                    tickersStorage.put(map)
                } catch (e: Throwable) {
                    // Обработка ошибки внутри цикла
                    println("Error occurred: ${e.message}")
                    // Можно добавить дополнительную логику, например, логирование или уведомление
                }
                delay(pollingMs) // Задержка после каждой итерации, включая случаи с ошибками
            }
        }
    }

    private fun mapToTickersMap(list: List<TickerData>) = list
        .associateBy { it.symbol }


    suspend fun get() = tickersStorage.get()
}
