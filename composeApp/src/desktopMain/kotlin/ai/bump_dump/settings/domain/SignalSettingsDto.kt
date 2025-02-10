package ai.bump_dump.settings.domain

import kotlinx.serialization.Serializable

@Serializable
data class SignalSettingsDto(
    val bumpSize: Float = 1f, // Размер бампа
    val dumpSize: Float = 1f, // Размер дампа
    val volumeRange: ClosedFloatingPointRange<Float> = 0f..Int.MAX_VALUE.toFloat(), // Диапазон объема
    val priceRange: ClosedFloatingPointRange<Float> = 0f..Int.MAX_VALUE.toFloat(), // Диапазон цены
    val minRating: Int = 1, // Минимальный рейтинг
    val maxRating: Int = Int.MAX_VALUE, // Максимальный рейтинг


    val isDarkTheme: Boolean = false
)