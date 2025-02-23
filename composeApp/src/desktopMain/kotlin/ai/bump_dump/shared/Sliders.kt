package ai.bump_dump.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SliderWithEditText(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    textValue: String,
    onTextChange: (String) -> Unit,
    valueRang: ClosedFloatingPointRange<Float> = 0f..100f,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.body1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = isEnabled,
                onCheckedChange = onEnabledChange
            )
        }
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
                onValueChange = { newText: String ->
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
                },
                modifier = Modifier.weight(1f),
                valueRange = minTextValue..maxTextValue
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = range.endInclusive.toString(),
                onValueChange = { newText: String ->
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