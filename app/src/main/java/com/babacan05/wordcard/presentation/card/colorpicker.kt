package com.babacan05.wordcard.presentation.card

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun CustomColorPicker(
    initialColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    Dialog(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Select a color:")
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val cellSize = size.width / COLORS.size
                            val index = (offset.x / cellSize).toInt()
                            if (index >= 0 && index < COLORS.size) {
                                selectedColor = COLORS[index].toInt()
                                onColorSelected(selectedColor)
                                onDismiss()
                            }
                        }
                    }
            ) {
                val cellSize = size.width / COLORS.size
                COLORS.forEachIndexed { index, color ->
                    drawRect(
                        color = Color(color),
                        topLeft = Offset(index * cellSize, 0f),
                        size = Size(cellSize, size.height)
                    )
                }
                drawCircle(
                    color = Color.Transparent,
                    radius = cellSize / 2,
                    center = Offset(
                        COLORS.indexOfFirst { it.toInt() == selectedColor } * cellSize + cellSize / 2,
                        size.height / 2
                    ),
                    style = Stroke(2f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

private val COLORS = listOf(
     0xFFE0E0E0,
0xFFB3E5FC,
 0xFFF8BBD0,
 0xFFC8E6C9,
    0xFF000000,
0xFFFFF9C4,
    0xFF295D6B,
    0xFFFF0000, // Red
    0xFF00FF00, // Green
    0xFF0000FF, // Blue
    0xFFFFFF00, // Yellow
    0xFFFF00FF, // Magenta
    0xFF00FFFF, // Cyan


)