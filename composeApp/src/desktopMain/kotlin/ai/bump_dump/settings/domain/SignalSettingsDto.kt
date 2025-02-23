package ai.bump_dump.settings.domain

import kotlinx.serialization.Serializable

@Serializable
data class SignalSettingsDto(
    val bumpSize: Float = 1f, // Размер бампа
    val dumpSize: Float = 1f, // Размер дампа
    val volumeMin: Float = 0f,
    val volumeMax: Float = Long.MAX_VALUE.toFloat(), // Мин/макс для объема
    val priceMin: Float = 0f,
    val priceMax: Float = Int.MAX_VALUE.toFloat(), // Мин/макс для цены
    val ratingMin: Float = 1f,
    val ratingMax: Float = 1000f, // Мин/макс для рейтинга

    val isDarkTheme: Boolean = false,
    val dumpEnabled: Boolean = true, // Включен ли дамп
    val bumpEnabled: Boolean = true, // Включен ли бамп
    val rationEnabled: Boolean = true // Включен ли расчет рейтинга
)
