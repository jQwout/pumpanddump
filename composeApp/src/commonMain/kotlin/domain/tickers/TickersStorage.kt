package domain.tickers

import shared.BoundedQueue

interface TickersStorage {

    suspend fun put(newTickMap: Map<String, TickerData>)

    suspend fun get(): TickersMap

    object Ram : TickersStorage {

        private val map = TickersMap()

        override suspend fun put(newTickMap: Map<String, TickerData>) {
            println("new data received : ${newTickMap.size}")
            newTickMap.forEach { (key, tick) ->
                val queue = map.getOrPut(key) { BoundedQueue(30) }
                queue.add(tick)
            }
        }

        override suspend fun get(): TickersMap {
            return map
        }
    }
}