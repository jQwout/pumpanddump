package domain.signals

import domain.tickers.TickerData
import domain.tickers.TickersByMinute
import domain.tickers.TickersByMinuteMap
import java.time.LocalDateTime

/**
 * пока считаем, что бамп это увеличение за 1 минуту курса более чем 1 процент
 * или за 2-3 более чем на 2.5-3 процента
 *
 * дамп - наоборот
 */

fun signalMapper(tickers: TickersByMinuteMap): List<SignalData>? {
    return computePercentForEach(tickers)
        ?.mapIndexed { index, it ->
            val priceChange = it.price
            val signal = when {
                priceChange.m1 > 1.0  || priceChange.m5 > 2.5  || priceChange.m10 > 4  -> Signal.BUMP
                priceChange.m1 < -1.0 || priceChange.m5 < -2.5 || priceChange.m10 < -4 -> Signal.DUMP
                else -> return@mapIndexed null
            }

            SignalData(
                date = LocalDateTime.now(), // Используем текущее время
                tickerName = it.tickerData.symbol,
                signal = signal,
                price = it.tickerData.markPrice,
                volume = it.tickerData.usdtVolume,
                placeOnStock = index,
                volumeChange = it.volume,
                priceChange = it.price,
                priceChange24h = it.tickerData.change24h,
                ts = it.tickerData.ts,
                fundingRate = it.tickerData.fundingRate
            )
        }?.filterNotNull()
}

fun PricePercentChange.m2Or3(): Double = maxOf(m3, m2())
fun PricePercentChange.m2(): Double = (m3 + m1) / 2


private fun computePercentForEach(actual30mFrame: TickersByMinuteMap): List<PercentChangeData>? {
    // Проверяем, что данные не пустые
    if (actual30mFrame.isEmpty()) {
        println("computePercentForEach: empty frame")
        return null
    }

    if (actual30mFrame.values.first().size < 2) {
        println("computePercentForEach: ${actual30mFrame.values.first().size}")
        return null
    }

    return actual30mFrame.mapNotNull { frame ->
        computePercent(frame.value)
    }
}

private fun computePercent(actual30mFrame: TickersByMinute): PercentChangeData {
    // Текущие данные (последний элемент в списке)
    val currentData = actual30mFrame.values.last()

    // Текущая цена и объем
    val m0 = currentData.lastPr.toDouble()
    val currentVolume = currentData.usdtVolume

    // Функция для вычисления процентного изменения
    fun calculateChange(current: Double, previous: Double): Double {
        return if (previous == 0.0) 0.0 else ((current - previous) / previous) * 100
    }

    // Получаем данные N минут назад (если данных недостаточно, возвращаем null)
    fun getDataNMinutesAgo(n: Int): MutableMap.MutableEntry<Int, TickerData>? {
        return actual30mFrame.entries.elementAtOrNull(actual30mFrame.size - (n + 1))
    }

    // Получаем данные для каждого временного интервала
    val m1 = getDataNMinutesAgo(1) // между 0 и 1 элементом
    val m3 = getDataNMinutesAgo(3) // между
    val m5 = getDataNMinutesAgo(5)
    val m10 = getDataNMinutesAgo(10)
    val m15 = getDataNMinutesAgo(15)
    val m20 = getDataNMinutesAgo(20)
    val m30 = getDataNMinutesAgo(30)

    // Вычисляем изменения для цены
    val priceChanges = PricePercentChange(
        m1 = m1?.value?.let { calculateChange(m0, it.lastPr.toDouble()) } ?: 0.0,
        m3 = m3?.value?.let { calculateChange(m0, it.lastPr.toDouble()) } ?: 0.0,
        m5 = m5?.value?.let { calculateChange(m0, it.lastPr.toDouble()) } ?: 0.0,
        m10 = m10?.value?.let { calculateChange(m0, it.lastPr.toDouble()) } ?: 0.0,
        m15 = m15?.value?.let { calculateChange(m0, it.lastPr.toDouble()) } ?: 0.0,
        m20 = m20?.value?.let { calculateChange(m0, it.lastPr.toDouble()) } ?: 0.0,
        m30 = m30?.value?.let { calculateChange(m0, it.lastPr.toDouble()) } ?: 0.0
    )

    // Вычисляем изменения для объема
    val volumeChanges = VolumePercentChange(
        m1 = m1?.value?.let { calculateChange(currentVolume, it.usdtVolume) } ?: 0.0,
        m5 = m5?.value?.let { calculateChange(currentVolume, it.usdtVolume) } ?: 0.0,
        m10 = m10?.value?.let { calculateChange(currentVolume, it.usdtVolume) } ?: 0.0,
        m30 = m30?.value?.let { calculateChange(currentVolume, it.usdtVolume) } ?: 0.0
    )

    // логгирование для дебага
    // логгирование по монете примерно выглядит так
    // -----
    // m0:   TickerData(symbol=UNIUSDT, lastPr=9.419, low24h=9.237, ts=1739314094618, change24h=-0.01061, usdtVolume=1.9996239592E7, changeUtc24h=-0.00317, indexPrice=9.431285, fundingRate=-2.2E-5, open24h=9.52, markPrice=9.423)
    // m1:47=TickerData(symbol=UNIUSDT, lastPr=9.419, low24h=9.237, ts=1739314073802, change24h=-0.01175, usdtVolume=1.9996877831E7, changeUtc24h=-0.00317, indexPrice=9.427428, fundingRate=-2.0E-5, open24h=9.531, markPrice=9.419)
    // m3:45=TickerData(symbol=UNIUSDT, lastPr=9.398, low24h=9.237, ts=1739313959132, change24h=-0.0153, usdtVolume=1.9992550193E7, changeUtc24h=-0.0054, indexPrice=9.407285, fundingRate=-1.6E-5, open24h=9.544, markPrice=9.398)
    // m5:43=TickerData(symbol=UNIUSDT, lastPr=9.428, low24h=9.237, ts=1739313833792, change24h=-0.00966, usdtVolume=2.017695905E7, changeUtc24h=-0.00222, indexPrice=9.436285, fundingRate=-1.3E-5, open24h=9.52, markPrice=9.427)
    // m10:38=TickerData(symbol=UNIUSDT, lastPr=9.387, low24h=9.237, ts=1739313536649, change24h=-0.01769, usdtVolume=1.9977182247E7, changeUtc24h=-0.00656, indexPrice=9.397571, fundingRate=-5.0E-6, open24h=9.556, markPrice=9.392)
    // -----
    // 38 - TickerData(symbol=UNIUSDT, lastPr=9.387, low24h=9.237, ts=1739313536649, change24h=-0.01769, usdtVolume=1.9977182247E7, changeUtc24h=-0.00656, indexPrice=9.397571, fundingRate=-5.0E-6, open24h=9.556, markPrice=9.392)
    // 39 - TickerData(symbol=UNIUSDT, lastPr=9.399, low24h=9.237, ts=1739313590976, change24h=-0.01354, usdtVolume=1.997715236E7, changeUtc24h=-0.00529, indexPrice=9.404666, fundingRate=-7.0E-6, open24h=9.528, markPrice=9.399)
    // 40 - TickerData(symbol=UNIUSDT, lastPr=9.442, low24h=9.237, ts=1739313653645, change24h=-0.01027, usdtVolume=1.9996308361E7, changeUtc24h=-0.00095, indexPrice=9.445857, fundingRate=-9.0E-6, open24h=9.54, markPrice=9.439)
    // 41 - TickerData(symbol=UNIUSDT, lastPr=9.436, low24h=9.237, ts=1739313717741, change24h=-0.01142, usdtVolume=2.0062395227E7, changeUtc24h=-0.00138, indexPrice=9.444285, fundingRate=-1.1E-5, open24h=9.545, markPrice=9.433)
    // 42 - TickerData(symbol=UNIUSDT, lastPr=9.431, low24h=9.237, ts=1739313770452, change24h=-0.0108, usdtVolume=2.0092453664E7, changeUtc24h=-0.0019, indexPrice=9.441285, fundingRate=-1.2E-5, open24h=9.534, markPrice=9.431)
    // 43 - TickerData(symbol=UNIUSDT, lastPr=9.428, low24h=9.237, ts=1739313833792, change24h=-0.00966, usdtVolume=2.017695905E7, changeUtc24h=-0.00222, indexPrice=9.436285, fundingRate=-1.3E-5, open24h=9.52, markPrice=9.427)
    // 44 - TickerData(symbol=UNIUSDT, lastPr=9.401, low24h=9.237, ts=1739313896591, change24h=-0.01766, usdtVolume=2.0155953868E7, changeUtc24h=-0.00466, indexPrice=9.412428, fundingRate=-1.4E-5, open24h=9.57, markPrice=9.401)
    // 45 - TickerData(symbol=UNIUSDT, lastPr=9.398, low24h=9.237, ts=1739313959132, change24h=-0.0153, usdtVolume=1.9992550193E7, changeUtc24h=-0.0054, indexPrice=9.407285, fundingRate=-1.6E-5, open24h=9.544, markPrice=9.398)
    // 46 - TickerData(symbol=UNIUSDT, lastPr=9.416, low24h=9.237, ts=1739314011116, change24h=-0.01269, usdtVolume=1.9996708863E7, changeUtc24h=-0.00349, indexPrice=9.425714, fundingRate=-1.8E-5, open24h=9.537, markPrice=9.418)
    // 47 - TickerData(symbol=UNIUSDT, lastPr=9.419, low24h=9.237, ts=1739314073802, change24h=-0.01175, usdtVolume=1.9996877831E7, changeUtc24h=-0.00317, indexPrice=9.427428, fundingRate=-2.0E-5, open24h=9.531, markPrice=9.419)
    // 48 - TickerData(symbol=UNIUSDT, lastPr=9.419, low24h=9.237, ts=1739314094618, change24h=-0.01061, usdtVolume=1.9996239592E7, changeUtc24h=-0.00317, indexPrice=9.431285, fundingRate=-2.2E-5, open24h=9.52, markPrice=9.423)
    // -----

    // println("-----")
    // println(currentData)
    // println("m1:$m1")
    // println("m3:$m3")
    // println("m5:$m5")
    // println("m10:$m10")
    // println("-----")
    // actual30mFrame.forEach { t, u ->
    //     println("$t - $u")
    // }
    // println("-----")

    return PercentChangeData(currentData, priceChanges, volumeChanges)
}

