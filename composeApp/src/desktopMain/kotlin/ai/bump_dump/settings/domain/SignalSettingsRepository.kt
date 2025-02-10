package ai.bump_dump.settings.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import shared.customJson
import java.io.File

class SignalSettingsRepository(
    private val file: File,
    private val coroutineScope: CoroutineScope
) {

    val signalSettings = MutableStateFlow<SignalSettingsDto?>(null)

    suspend fun save(settings: SignalSettingsDto) {
        withContext(coroutineScope.coroutineContext) {
            signalSettings.value = settings
            file.writeText(customJson.encodeToString(settings))
        }
    }

    suspend fun load(): SignalSettingsDto {
        val cached = signalSettings.value
        if (cached != null) {
            return cached
        }

        return withContext(coroutineScope.coroutineContext) {
            val settings = if (file.exists()) {
                customJson.decodeFromString(file.readText())
            } else {
                SignalSettingsDto()
            }
            signalSettings.value = settings
            settings
        }
    }
}
