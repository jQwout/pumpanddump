package domain.tickers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import shared.getMinuteFromTimestamp

// описание контракта работы со стораджем
// компоненты  пприложение знают только контракт, но не имеют понятия о реализации
// это нудно, что бы в случае перехода напирмер на реализацию хранилища через базу данных
// другие компоненты не приходилось переписывать
//
// рабоатет так - принимаеть данные от сетевого хранилища,
// накапливает их
interface TickersStorage {

    suspend fun put(newTickMap: Map<String, TickerData>)

    suspend fun get(): TickersLast30Minute

    // потом данных, оповестит подписчика при изменениях.
    val updatesFlow: Flow<TickersLast30Minute>


    // хранилище данных в оперативной памяти
    // реализует контракт TickersStorage
    object Ram : TickersStorage {

        /**
         *
         *  {
         *    "btc": {
         *      "tM": { "данные за tM" },
         *      "tM+1": { "данные за tM+1" },
         *      "tM+2": { "данные за tM+2" }
         *     }
         *  }
         */


        private val _stateFlow = MutableStateFlow(TickersLast30Minute(0, LinkedHashMap()))


        override suspend fun put(newTickMap: Map<String, TickerData>) {
            _stateFlow.update {
                val newTickers = it
                newTickMap.forEach { (key, tick) ->
                    // ключ - наименование токена
                    // тикс- данные по токену

                    // timeToTick данные по монете с разбивкой на временные промежутки размеров в полчаса
                    // tM:   { "данные за tM"   },
                    // tM+1: { "данные за tM+1" },
                    // tM+2: { "данные за tM+2" }
                    //
                    val timeToTick =
                        newTickers.map.get(key)
                            ?: LinkedHashMap() // если нету данных - создаем временную разбивку для монеты

                    // минута - ключ в разбивке данных
                    val minute = getMinuteFromTimestamp(newTickMap.values.first().ts)

                    // вставляем данные на текущую минуту,
                    timeToTick.put(minute, tick)

                    newTickers.map.put(key, timeToTick)
                }
                newTickers
            }
        }

        override suspend fun get(): TickersLast30Minute {
            return _stateFlow.value
        }

        override val updatesFlow: Flow<TickersLast30Minute>
            get() = _stateFlow
    }
}