package domain.tickers

import shared.BoundedQueue
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
typealias TickersMap = HashMap<String, TickersFrame>
typealias TickersFrame = HashMap<Int, BoundedQueue<TickerData>>

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
    val markPrice: Double
)

