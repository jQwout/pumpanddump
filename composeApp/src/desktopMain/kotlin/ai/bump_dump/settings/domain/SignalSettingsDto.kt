package ai.bump_dump.settings.domain

import kotlinx.serialization.Serializable

@Serializable
data class SignalSettingsDto(
    val bumpSize: Float = 1f, // Размер бампа
    val dumpSize: Float = 1f, // Размер дампа
    val volumeRange: ClosedFloatingPointRange<Float> = 0f..Long.MAX_VALUE.toFloat(), // Диапазон объема
    val priceRange: ClosedFloatingPointRange<Float> = 0f..Int.MAX_VALUE.toFloat(), // Диапазон цены
    val ratingRange: ClosedFloatingPointRange<Float> = 0f..1000f,


    val isDarkTheme: Boolean = false
)