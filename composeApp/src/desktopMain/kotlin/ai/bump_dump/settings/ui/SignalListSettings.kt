package ai.bump_dump.settings.ui

import ai.bump_dump.screener.ui.SignalListPresenter
import ai.bump_dump.settings.domain.SignalSettingsDto
import ai.bump_dump.shared.Callback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var bumpSizeText by remember { mutableStateOf(bumpSize.toString()) }

    var dumpSize by remember { mutableStateOf(settingsDto.dumpSize) }
    var dumpSizeText by remember { mutableStateOf(dumpSize.toString()) }

    var volumeRange by remember { mutableStateOf(settingsDto.volumeRange) }
    var minVolumeText by remember { mutableStateOf(volumeRange.start.toString()) }
    var maxVolumeText by remember { mutableStateOf(volumeRange.endInclusive.toString()) }

    var ratingRating by remember { mutableStateOf(settingsDto.ratingRange) }

    var isDarkTheme by remember { mutableStateOf(settingsDto.isDarkTheme) }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SliderWithEditText(
                label = "Bump Size",
                value = bumpSize,
                onValueChange = { bumpSize = it },
                textValue = bumpSizeText,
                onTextChange = { bumpSizeText = it },
                valueRang = 0f..100f
            )

            SliderWithEditText(
                label = "Dump Size",
                value = dumpSize,
                onValueChange = { dumpSize = it },
                textValue = dumpSizeText,
                onTextChange = { dumpSizeText = it },
                valueRang = 0f..100f
            )

            RangeSliderWithEditText(
                label = "Volume Range",
                range = volumeRange,
                onRangeChange = { volumeRange = it },
                minTextValue = 1000f,
                onMinTextChange = { minVolumeText = it },
                maxTextValue = Long.MAX_VALUE.toFloat(),
                onMaxTextChange = { maxVolumeText = it }
            )

            RangeSliderWithEditText(
                label = "Rating Range",
                minTextValue = 1f,
                maxTextValue = 500f,
                onMinTextChange = { ratingRating = it.toFloat()..ratingRating.endInclusive },
                onMaxTextChange = { ratingRating = ratingRating.start..it.toFloat() },
                range = ratingRating,
                onRangeChange = {
                    ratingRating = it
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Dark Theme", style = MaterialTheme.typography.body1)
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { isDarkTheme = it }
                )
            }

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
                        volumeRange = volumeRange,
                        ratingRange = ratingRating,
                        isDarkTheme = isDarkTheme
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
fun SliderWithEditText(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    textValue: String,
    onTextChange: (String) -> Unit,
    valueRang: ClosedFloatingPointRange<Float> = 0f..100f,
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.body1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                    onTextChange(newValue.toString())
                },
                modifier = Modifier.weight(1f),
                valueRange = valueRang
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = textValue,
                onValueChange = { newText ->
                    onTextChange(newText)
                    newText.toFloatOrNull()?.let { onValueChange(it) }
                },
                maxLines = 1,
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
        }
    }
}

@Composable
fun SliderWithEditTextAndSteps(
    label: String,
    value: Float,
    valueRang: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    textValue: String,
    onTextChange: (String) -> Unit,
    steps: Int
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.body1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                    onTextChange(newValue.toString())
                },
                modifier = Modifier.weight(1f),
                valueRange = valueRang,
                steps = steps  // Добавляем отсечки
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = textValue,
                onValueChange = { newText ->
                    onTextChange(newText)
                    newText.toFloatOrNull()?.let { onValueChange(it) }
                },
                maxLines = 1,
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RangeSliderWithEditText(
    label: String,
    range: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    minTextValue: Float,
    onMinTextChange: (String) -> Unit,
    maxTextValue: Float,
    onMaxTextChange: (String) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.body1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = range.start.toString(),
                onValueChange = { newText : String ->
                    onMinTextChange(newText)
                    newText.toFloatOrNull()?.let { newMin ->
                        onRangeChange(newMin..range.endInclusive)
                    }
                },
                maxLines = 1,
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
            Spacer(modifier = Modifier.width(8.dp))
            RangeSlider(
                value = range,
                onValueChange = { newRange ->
                    onRangeChange(newRange)
                    onMinTextChange(newRange.start.toString())
                    onMaxTextChange(newRange.endInclusive.toString())
                },
                modifier = Modifier.weight(1f),
                valueRange = minTextValue..maxTextValue
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = range.endInclusive.toString(),
                onValueChange = { newText : String ->
                    onMaxTextChange(newText)
                    newText.toFloatOrNull()?.let { newMax ->
                        onRangeChange(range.start..newMax)
                    }
                },
                maxLines = 1,
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
        }
    }
}

@Composable
fun RangeEditTextInt(
    label: String,
    minTextValue: String,
    onMinTextChange: (String) -> Unit,
    maxTextValue: String,
    onMaxTextChange: (String) -> Unit,
    minValueLimit: Int = 1,
    maxValueLimit: Int = 500
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.body1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = minTextValue,
                onValueChange = { newText ->
                    if (minValueLimit <= newText.toInt()) {
                        onMinTextChange(newText)
                    }
                },
                maxLines = 1,
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = maxTextValue,
                onValueChange = { newText ->
                    if (maxValueLimit >= newText.toInt()) {
                        onMaxTextChange(newText)
                    }
                },
                maxLines = 1,
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
        }
    }
}