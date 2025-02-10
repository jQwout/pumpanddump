package domain.tickers

import shared.BoundedQueue
import kotlinx.serialization.Serializable

/**
 *
 * удобнее будет хранить в формате
 *  {
 *   btc : [ {данные за tx}, {данные за tx+1}, {данные за tx+2},],
 *   eth : [ {данные за tx}, {данные за tx+1}, {данные за tx+2},],
 *   xpr : [ {данные за tx}, {данные за tx+1}, {данные за tx+2},],
 *  }
 */
typealias TickersFrame = BoundedQueue<TickerData>
typealias TickersMap = HashMap<String, TickersFrame>


@Serializable
data class TickerData(
    val symbol: String,
    val lastPr: String,
    // val askPr: String,
    //val bidPr: String,
    //val bidSz: String,
    //val askSz: String,
    //val high24h: String,
    val low24h: String,
    val ts: Long,
    val change24h: Double,
    //val baseVolume: String,
    //val quoteVolume: String,
    val usdtVolume: Double,
    //val openUtc: String,
    val changeUtc24h: String,
    val indexPrice: String,
    val fundingRate: Double,
    // val holdingAmount: String,
    //val deliveryStartTime: Long? = null,
    //val deliveryTime: Long? = null,
    //val deliveryStatus: String,
    val open24h: String,
    val markPrice: Double
)

