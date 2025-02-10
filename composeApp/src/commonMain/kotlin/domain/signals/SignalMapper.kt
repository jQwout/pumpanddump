package domain.signals

import domain.tickers.TickerData
import domain.tickers.TickersMap
import java.time.LocalDateTime

/**
 * пока считаем, что бамп это увеличение за 1 минуту курса более чем 1 процент
 * или за 2-3 более чем на 2.5-3 процента
 *
 * дамп - наоборот
 */

fun signalMapper(tickers: TickersMap): List<SignalData>? {
    return computePercentForEach(tickers)
        ?.sortedByDescending { it.tickerData.change24h } // Сортировка по change24h
        ?.mapIndexed { index, it ->
            val priceChange = it.price
            val signal = when {
                priceChange.m1 > 1.0 || (priceChange.m2Or3() > 1) || priceChange.m5 > 1 -> Signal.BUMP
                priceChange.m1 < -1.0 || (priceChange.m2Or3() < -1) || priceChange.m5 < -1 -> Signal.DUMP
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

private fun PricePercentChange.m2Or3(): Double = maxOf(m3, m2())
private fun PricePercentChange.m2(): Double = (m3 + m1) / 2



private fun computePercentForEach(actual30mFrame: TickersMap): List<PercentChangeData>? {
    // Проверяем, что данные не пустые
    if (actual30mFrame.isEmpty()) {
        return null
    }

    return actual30mFrame.mapNotNull { frame ->
        computePercent(frame.value.getAll())
    }
}

private fun computePercent(actual30mFrame: List<TickerData>): PercentChangeData {
    // Проверяем, что в списке есть хотя бы 3 элемента (3 минуты)
    //require(actual30mFrame.size >= 3) { "Недостаточно данных для вычисления изменений. Минимум 3 элемента." }

    // Текущие данные (последний элемент в списке)
    val currentData = actual30mFrame.last()

    // Текущая цена и объем
    val currentPrice = currentData.lastPr.toDouble()
    val currentVolume = currentData.usdtVolume.toDouble()

    // Функция для вычисления процентного изменения
    fun calculateChange(current: Double, previous: Double): Double {
        return if (previous == 0.0) 0.0 else ((current - previous) / previous) * 100
    }

    // Получаем данные N минут назад (если данных недостаточно, возвращаем null)
    fun getDataNMinutesAgo(n: Int): TickerData? {
        return if (actual30mFrame.size > n) actual30mFrame[actual30mFrame.size - 1 - n] else null
    }

    // Вычисляем изменения для цены
    val priceChanges = PricePercentChange(
        m1 = getDataNMinutesAgo(1)?.let { calculateChange(currentPrice, it.lastPr.toDouble()) } ?: 0.0,
        m3 = getDataNMinutesAgo(3)?.let { calculateChange(currentPrice, it.lastPr.toDouble()) } ?: 0.0,
        m5 = getDataNMinutesAgo(5)?.let { calculateChange(currentPrice, it.lastPr.toDouble()) } ?: 0.0,
        m10 = getDataNMinutesAgo(10)?.let { calculateChange(currentPrice, it.lastPr.toDouble()) } ?: 0.0,
        m15 = getDataNMinutesAgo(15)?.let { calculateChange(currentPrice, it.lastPr.toDouble()) } ?: 0.0,
        m20 = getDataNMinutesAgo(20)?.let { calculateChange(currentPrice, it.lastPr.toDouble()) } ?: 0.0,
        m30 = getDataNMinutesAgo(30)?.let { calculateChange(currentPrice, it.lastPr.toDouble()) } ?: 0.0
    )

    // Вычисляем изменения для объема
    val volumeChanges = VolumePercentChange(
        m1 = getDataNMinutesAgo(1)?.let { calculateChange(currentVolume, it.usdtVolume.toDouble()) } ?: 0.0,
     //   m3 = getDataNMinutesAgo(3)?.let { calculateChange(currentVolume, it.usdtVolume.toDouble()) } ?: 0.0,
        m5 = getDataNMinutesAgo(5)?.let { calculateChange(currentVolume, it.usdtVolume.toDouble()) } ?: 0.0,
        m10 = getDataNMinutesAgo(10)?.let { calculateChange(currentVolume, it.usdtVolume.toDouble()) } ?: 0.0,
    //    m15 = getDataNMinutesAgo(15)?.let { calculateChange(currentVolume, it.usdtVolume.toDouble()) } ?: 0.0,
    //    m20 = getDataNMinutesAgo(20)?.let { calculateChange(currentVolume, it.usdtVolume.toDouble()) } ?: 0.0,
        m30 = getDataNMinutesAgo(30)?.let { calculateChange(currentVolume, it.usdtVolume.toDouble()) } ?: 0.0
    )

    return PercentChangeData(currentData, priceChanges, volumeChanges)
}

