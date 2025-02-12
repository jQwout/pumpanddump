package shared

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

// Функция для получения минуты из timestamp
fun getMinuteFromTimestamp(ts: Long): Int {
    // Преобразуем timestamp в Instant
    val instant = Instant.ofEpochMilli(ts)
    // Преобразуем Instant в LocalDateTime в нужной временной зоне (например, UTC)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
    // Извлекаем минуту
    return dateTime.minute
}