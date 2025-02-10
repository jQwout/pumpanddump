package ai.bump_dump.settings.ui

import ai.bump_dump.settings.domain.SignalSettingsDto
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SignalListSettingsScreen(
    onDismissRequest: () -> Unit,
    onSave: (SignalSettingsDto) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        SettingsDialog(onDismissRequest, onSave)
    }
}

@Composable
@Preview
fun SettingsDialog(onDismissRequest: () -> Unit, onSave: (SignalSettingsDto) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Размер бампа
            var bumpSize by remember { mutableStateOf(50f) }
            var bumpSizeText by remember { mutableStateOf(bumpSize.toString()) }
            SliderWithEditText(
                label = "Bump Size",
                value = bumpSize,
                onValueChange = { bumpSize = it },
                textValue = bumpSizeText,
                onTextChange = { bumpSizeText = it },
                valueRang = 0f..100f
            )

            // 2. Размер дампа
            var dumpSize by remember { mutableStateOf(50f) }
            var dumpSizeText by remember { mutableStateOf(dumpSize.toString()) }
            SliderWithEditText(
                label = "Dump Size",
                value = dumpSize,
                onValueChange = { dumpSize = it },
                textValue = dumpSizeText,
                onTextChange = { dumpSizeText = it },
                valueRang = 0f..100f
            )

            // 3. Минимальный и максимальный объем
            var volumeRange by remember { mutableStateOf(0f..100f) }
            var minVolumeText by remember { mutableStateOf(volumeRange.start.toString()) }
            var maxVolumeText by remember { mutableStateOf(volumeRange.endInclusive.toString()) }
            RangeSliderWithEditText(
                label = "Volume Range",
                range = volumeRange,
                onRangeChange = { volumeRange = it },
                minTextValue = minVolumeText,
                onMinTextChange = { minVolumeText = it },
                maxTextValue = maxVolumeText,
                onMaxTextChange = { maxVolumeText = it }
            )

            // 4. Минимальная и максимальная цена
            var priceRange by remember { mutableStateOf(0f..100f) }
            var minPriceText by remember { mutableStateOf(priceRange.start.toString()) }
            var maxPriceText by remember { mutableStateOf(priceRange.endInclusive.toString()) }
            RangeSliderWithEditText(
                label = "Price Range",
                range = priceRange,
                onRangeChange = { priceRange = it },
                minTextValue = minPriceText,
                onMinTextChange = { minPriceText = it },
                maxTextValue = maxPriceText,
                onMaxTextChange = { maxPriceText = it }
            )

            // 5. Минимальный и максимальный рейтинг
            var minRatingText by remember { mutableStateOf(1) }
            var maxRatingText by remember { mutableStateOf(500) }
            RangeEditTextInt(
                label = "Rating Range",
                minTextValue = minRatingText.toString(),
                onMinTextChange = {
                    minRatingText = it.toInt()
                },
                maxTextValue = maxRatingText.toString(),
                onMaxTextChange = {
                    maxRatingText = it.toInt()
                },
                minValueLimit = 1,
                maxValueLimit = 500
            )

            // 6. Переключатель темы
            var isDarkTheme by remember { mutableStateOf(false) }
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

            // Кнопки "Сохранить" и "Отмена"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onDismissRequest) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    // Создание объекта SignalSettingsDto с текущими значениями
                    val settings = SignalSettingsDto(
                        bumpSize = bumpSize,
                        dumpSize = dumpSize,
                        volumeRange = volumeRange,
                        priceRange = priceRange,
                        minRating = minRatingText,
                        maxRating = maxRatingText
                    )
                    // Вызов функции onSave с передачей настроек
                    onSave(settings)
                    // Закрытие диалога
                    onDismissRequest()
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
            TextField(
                value = textValue,
                onValueChange = { newText ->
                    onTextChange(newText)
                    newText.toFloatOrNull()?.let { onValueChange(it) }
                },
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
            TextField(
                value = textValue,
                onValueChange = { newText ->
                    onTextChange(newText)
                    newText.toFloatOrNull()?.let { onValueChange(it) }
                },
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
    minTextValue: String,
    onMinTextChange: (String) -> Unit,
    maxTextValue: String,
    onMaxTextChange: (String) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.body1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = minTextValue,
                onValueChange = { newText ->
                    onMinTextChange(newText)
                    newText.toFloatOrNull()?.let { newMin ->
                        onRangeChange(newMin..range.endInclusive)
                    }
                },
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
                valueRange = 0f..100f
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = maxTextValue,
                onValueChange = { newText ->
                    onMaxTextChange(newText)
                    newText.toFloatOrNull()?.let { newMax ->
                        onRangeChange(range.start..newMax)
                    }
                },
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
        }
    }
}

@Composable
fun RangeEditTextDouble(
    label: String,
    minTextValue: String,
    onMinTextChange: (String) -> Unit,
    maxTextValue: String,
    onMaxTextChange: (String) -> Unit,
    minValueLimit: Double = 0.0,
    maxValueLimit: Double = 300_000.0
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.body1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = minTextValue,
                onValueChange = { newText ->
                    if (minValueLimit <= newText.toDouble()) {
                        onMinTextChange(newText)
                    }
                },
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = maxTextValue,
                onValueChange = { newText ->
                    if (maxValueLimit >= newText.toDouble()) {
                        onMaxTextChange(newText)
                    }
                },
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
            TextField(
                value = minTextValue,
                onValueChange = { newText ->
                    if (minValueLimit <= newText.toInt()) {
                        onMinTextChange(newText)
                    }
                },
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = maxTextValue,
                onValueChange = { newText ->
                    if (maxValueLimit >= newText.toInt()) {
                        onMaxTextChange(newText)
                    }
                },
                modifier = Modifier.width(80.dp),  // Уменьшенный размер
                textStyle = TextStyle(fontSize = 14.sp)  // Уменьшенный размер текста
            )
        }
    }
}