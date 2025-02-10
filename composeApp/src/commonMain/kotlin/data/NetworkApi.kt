package data

import domain.tickers.TickerData
import io.ktor.client.call.*
import io.ktor.client.request.*
import shared.ktorClient

suspend fun tickers(): MarketDataResponse {
    val response = ktorClient.get("https://api.bitget.com/api/v2/mix/market/tickers?productType=USDT-FUTURES")
    return response.body<MarketDataResponse>()
}

// модели

// Корневой объект JSON
@kotlinx.serialization.Serializable
data class MarketDataResponse(
    val code: String,
    val msg: String,
    val requestTime: Long,
    val data: List<TickerData>
)
