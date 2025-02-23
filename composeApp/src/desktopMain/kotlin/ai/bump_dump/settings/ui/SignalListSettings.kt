package ai.bump_dump.settings.ui


import ai.bump_dump.settings.domain.SignalSettingsDto
import ai.bump_dump.shared.Callback
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun SignalListSettingsScreen(
    signalListSettingsPresenter: SignalListSettingsPresenter,
    onClose: Callback
) {
    val cfg = signalListSettingsPresenter.settings ?: return
    SettingsView(cfg, onClose, signalListSettingsPresenter::saveSettings)
}


@Composable
fun SignalListSettingsDialog(
    settingsDto: SignalSettingsDto,
    onClose: () -> Unit,
    onSave: (SignalSettingsDto) -> Unit,
) {
    Dialog(onDismissRequest = onClose) {
        SettingsView(settingsDto, onClose, onSave)
    }
}

@Composable
@Preview
fun SettingsView(
    settingsDto: SignalSettingsDto,
    onClose: () -> Unit,
    onSave: (SignalSettingsDto) -> Unit
) {
    var bumpSize by remember { mutableStateOf(settingsDto.bumpSize) }
    var dumpSize by remember { mutableStateOf(settingsDto.dumpSize) }

    var volumeMin by remember { mutableStateOf(settingsDto.volumeMin) }
    var volumeMax by remember { mutableStateOf(settingsDto.volumeMax) }

    var ratingMin by remember { mutableStateOf(settingsDto.ratingMin) }
    var ratingMax by remember { mutableStateOf(settingsDto.ratingMax) }

    var isDarkTheme by remember { mutableStateOf(settingsDto.isDarkTheme) }
    var dumpEnabled by remember { mutableStateOf(settingsDto.dumpEnabled) } // Состояние для dumpEnabled
    var bumpEnabled by remember { mutableStateOf(settingsDto.bumpEnabled) } // Состояние для bumpEnabled
    var rationEnabled by remember { mutableStateOf(settingsDto.rationEnabled) } // Состояние для rationEnabled

    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        ) {
            // Bump Size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Bump Size", style = MaterialTheme.typography.body1)
                Checkbox(
                    checked = bumpEnabled,
                    onCheckedChange = { bumpEnabled = it }
                )
            }
            FloatInputField(
                value = bumpSize,
                onValueChange = { bumpSize = it },
                minValue = 0f,
                maxValue = 100f,
                enabled = bumpEnabled // Передаем состояние bumpEnabled
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Dump Size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Dump Size", style = MaterialTheme.typography.body1)
                Checkbox(
                    checked = dumpEnabled,
                    onCheckedChange = { dumpEnabled = it }
                )
            }
            FloatInputField(
                value = dumpSize,
                onValueChange = { dumpSize = it },
                minValue = 0f,
                maxValue = 100f,
                enabled = dumpEnabled // Передаем состояние dumpEnabled
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Rating Range
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Rating Range", style = MaterialTheme.typography.body1)
                Checkbox(
                    checked = rationEnabled,
                    onCheckedChange = { rationEnabled = it }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                NumericInputField(
                    value = ratingMin.toInt(),
                    onValueChange = { ratingMin = it.toFloat() },
                    minValue = 1,
                    maxValue = ratingMax.toInt(),
                    enabled = rationEnabled // Передаем состояние rationEnabled
                )
                Spacer(modifier = Modifier.width(8.dp))
                NumericInputField(
                    value = ratingMax.toInt(),
                    onValueChange = { ratingMax = it.toFloat() },
                    minValue = ratingMin.toInt(),
                    maxValue = 1000,
                    enabled = rationEnabled // Передаем состояние rationEnabled
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Dark Theme
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Dark Theme", style = MaterialTheme.typography.body1)
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { isDarkTheme = it }
                )
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onClose) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val settings = SignalSettingsDto(
                        bumpSize = bumpSize,
                        dumpSize = dumpSize,
                        volumeMin = volumeMin,
                        volumeMax = volumeMax,
                        ratingMin = ratingMin,
                        ratingMax = ratingMax,
                        isDarkTheme = isDarkTheme,
                        dumpEnabled = dumpEnabled, // Сохраняем состояние dumpEnabled
                        bumpEnabled = bumpEnabled, // Сохраняем состояние bumpEnabled
                        rationEnabled = rationEnabled // Сохраняем состояние rationEnabled
                    )
                    onSave(settings)
                    onClose()
                }) {
                    Text("Save")
                }
            }
        }
    }
}
@Composable
fun NumericInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = 1000,
    step: Int = 1,
    enabled: Boolean = true // Добавлен параметр enabled
) {
    Row(
        modifier = modifier
            .border(1.dp, MaterialTheme.colors.surface, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { newValue ->
                if (enabled) { // Проверка на enabled
                    newValue.toIntOrNull()?.let { num ->
                        if (num in minValue..maxValue) {
                            onValueChange(num)
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(100.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            singleLine = true,
            enabled = enabled // Передаем состояние enabled в OutlinedTextField
        )
        Column {
            IconButton(
                onClick = {
                    if (enabled && value < maxValue) { // Проверка на enabled
                        onValueChange(value + step)
                    }
                },
                enabled = enabled // Передаем состояние enabled в IconButton
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
            }
            IconButton(
                onClick = {
                    if (enabled && value > minValue) { // Проверка на enabled
                        onValueChange(value - step)
                    }
                },
                enabled = enabled // Передаем состояние enabled в IconButton
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
            }
        }
    }
}

@Composable
fun FloatInputField(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = 1000f,
    step: Float = 0.5f,
    enabled: Boolean = true // Добавлен параметр enabled
) {
    Row(
        modifier = modifier
            .border(1.dp, MaterialTheme.colors.surface, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { newValue ->
                if (enabled) { // Проверка на enabled
                    newValue.toFloatOrNull()?.let { num ->
                        if (num in minValue..maxValue) {
                            onValueChange(num)
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(100.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            singleLine = true,
            enabled = enabled // Передаем состояние enabled в OutlinedTextField
        )
        Column {
            IconButton(
                onClick = {
                    if (enabled && value + step <= maxValue) { // Проверка на enabled
                        onValueChange((value + step).coerceAtMost(maxValue))
                    }
                },
                enabled = enabled // Передаем состояние enabled в IconButton
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
            }
            IconButton(
                onClick = {
                    if (enabled && value - step >= minValue) { // Проверка на enabled
                        onValueChange((value - step).coerceAtLeast(minValue))
                    }
                },
                enabled = enabled // Передаем состояние enabled в IconButton
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
            }
        }
    }
}
