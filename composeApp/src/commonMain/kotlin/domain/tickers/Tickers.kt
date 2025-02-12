package domain.tickers

import kotlinx.serialization.Serializable

/**
 *
 * удобнее будет хранить в формате
 *  {
 *    "btc": {
 *      "tM": { "данные за tM" },
 *      "tM+1": { "данные за tM+1" },
 *      "tM+2": { "данные за tM+2" }
 *      }
 *  }
 */
typealias TickersByMinuteMap = HashMap<String, TickersByMinute>
typealias TickersByMinute = LinkedHashMap<Int, TickerData>

// данные от биржи апи
@Serializable
data class TickerData(
    val symbol: String,
    val lastPr: String,
    val low24h: String,
    val ts: Long,
    val change24h: Double,
    val usdtVolume: Double,
    val changeUtc24h: String,
    val indexPrice: String,
    val fundingRate: Double,
    val open24h: String,
    val markPrice: Double,
    val holdingAmount: Double
)

// данные от внутреннего хранилища

class TickersLast30Minute(
    val ts: Long,
    val map : TickersByMinuteMap
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TickersLast30Minute

        return ts == other.ts
    }

    override fun hashCode(): Int {
        return ts.hashCode()
    }
}