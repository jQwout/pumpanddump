package ai.bump_dump.screener.ui

import ai.bump_dump.settings.ui.SignalListSettingsScreen
import ai.bump_dump.shared.Callback
import ai.bump_dump.shared.openBrowser
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.signals.Signal
import domain.signals.SignalData
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.time.format.DateTimeFormatter

@Composable
fun SignalListScreen(
    signalListPresenter: SignalListPresenter,
    onSettingsClick: Callback
) {
    Surface() {
        Button(
            onClick = {
                onSettingsClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("НАСТРОЙКИ")
        }
        // Отображаем список сигналов
        SignalListView(signalListPresenter.signals)
        // настройки
    }
}

@Composable
@Preview
private fun SignalListView(items: List<SignalData> = emptyList()) {
    val listState = rememberLazyListState()

    // Автоматическая прокрутка вниз при изменении списка
    LaunchedEffect(items.size) {
        if (items.isNotEmpty()) {
            listState.animateScrollToItem(items.size - 1)
        }
    }

    LazyColumn(
        modifier = Modifier.padding(56.dp).fillMaxHeight(),
        state = listState// Паддинг для LazyColumn
    ) {
        items(
            items = items,
            key = { it.key }
        ) { signal ->
            SignalMessageCard(signal)
            //SignalMessage(message = signal.toMessage(), tickerName = signal.tickerName)
            if (signal.key != items.last().key) {
                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SignalMessageCard(signal: SignalData) {
    Card(
        shape = RoundedCornerShape(12.dp),
        contentColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.onSurface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Название тикера и место на бирже
            Text(
                text = "${signal.tickerName} (${signal.placeOnStock})${if (signal.signal == Signal.BUMP) "⬆️" else "⬇️"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )

            // Цена с цветом и процентом изменения
            val priceChangem1 = signal.priceChange.m1
            val priceColor = when {
                priceChangem1 > 0 -> Color(0xFF4CAF50) // Зеленый, если цена растет
                priceChangem1 < 0 -> Color(0xFFF44336) // Красный, если цена падает
                else -> Color.Black // Белый, если цена не изменилась
            }
            Text(
                text = "💰 Price: ${"%,.10f".format(signal.price)} (%${"%.2f".format(signal.priceChange24h * 100)}) USDT",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = priceColor
            )

            // Изменение цены
            Text(
                text = "📊 Price Change: 1m: ${"%.2f".format(signal.priceChange.m1)}%, 3m: ${"%.2f".format(signal.priceChange.m3)}%, 5m: ${
                    "%.2f".format(
                        signal.priceChange.m5
                    )
                }%, 24h: ${"%.2f".format(signal.priceChange24h)}%",
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary
            )

            // Объем
            Text(
                text = "📦 Volume: ${"%,.2f".format(signal.volume)} USDT",
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary
            )

            // Изменение объема
            Text(
                text = "🔄 Volume Change: 1m: ${"%.2f".format(signal.volumeChange.m1)}%,  5m: ${
                    "%.2f".format(
                        signal.volumeChange.m5
                    )
                }%",
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary
            )

            // Фандинг рейт
            Text(
                text = "⚖️ Funding Rate: ${"%.6f".format(signal.fundingRate)}%",
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary
            )

            // Кнопки ссылок
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinkButton("Bybit", "https://www.bybit.com/trade/usdt/${signal.tickerName}")
                LinkButton("Bitget", "https://www.bitget.com/futures/usdt/${signal.tickerName}")
            }
        }

        // Время в правом нижнем углу
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = signal.date.format(formatter), // Предполагаем, что signal.time содержит время в формате строки
                fontSize = 12.sp,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }
    }
}

fun SignalData.toMessage(): String {
    val trendEmoji = if (priceChange.m1 >= 0) "📈" else "📉"
    return """
            |---------------${signal}-------------
            | ⏰ Time: ${date.format(formatter)}
            | $trendEmoji Ticker: $tickerName (${placeOnStock})        
            | 💰 Price: ${"%,.10f".format(price)} USDT
            | 📊 Price Change: 
            |    1m: ${"%.2f".format(priceChange.m1)}% | 3m: ${"%.2f".format(priceChange.m3)}% | 5m: ${
        "%.2f".format(
            priceChange.m5
        )
    }% | 24h: ${"%.2f".format(priceChange24h)}%                 
            | 📦 Volume: ${"%,.2f".format(volume)} USDT
            | 🔄 Volume Change:
            |    1m: ${"%.2f".format(volumeChange.m1)}% | 5m: ${
        "%.2f".format(
            volumeChange.m5
        )
    }%
            | ⚖️ Funding Rate: ${"%.6f".format(fundingRate)}%
            |-------------------------------
            """.trimMargin()
}

private val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")

@Composable
fun SignalMessage(message: String, tickerName: String) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = message,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(8.dp))
        Row {
            LinkButton("bybit", "https://www.bybit.com/trade/usdt/${tickerName}")
            Spacer(Modifier.width(8.dp))
            LinkButton("bitget", "https://www.bitget.com/futures/usdt/${tickerName}")
        }
    }
}

@Composable
fun LinkButton(name: String, link: String) {
    Button(
        onClick = {
            openBrowser(link)
        },
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(name)
    }
}