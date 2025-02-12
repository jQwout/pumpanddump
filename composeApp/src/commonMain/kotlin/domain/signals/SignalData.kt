package domain.signals

import domain.tickers.TickerData
import java.time.LocalDateTime

data class SignalData(
    val date: LocalDateTime,
    val tickerName: String,
    val signal: Signal,
    val price: Double,
    val priceChange: PricePercentChange,
    val volume: Double,
    val volumeChange: VolumePercentChange,
    val placeOnStock: Int,
    val priceChange24h: Double,
    val ts: Long,
    val fundingRate: Double,
) {
    val key : String get() = tickerName+ts
}


enum class Signal {
    DUMP, BUMP
}

class PercentChangeData(
    val tickerData: TickerData,
    val price: PricePercentChange,
    val volume: VolumePercentChange,
)

class PricePercentChange(
    val m1: Double,
    val m3: Double,
    val m5: Double,
    val m10: Double,
    val m15: Double,
    val m20: Double,
    val m30: Double
)

class VolumePercentChange(
    val m1: Double,
    val m5: Double,
    val m10: Double,
    val m30: Double
)