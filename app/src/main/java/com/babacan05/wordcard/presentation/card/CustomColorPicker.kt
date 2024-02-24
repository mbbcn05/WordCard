package com.babacan05.wordcard.presentation.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight


@Composable
fun CustomColorPicker(
    initialColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    updatecolor: () -> Unit,
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    Dialog(onDismissRequest = onDismiss) {
        Card(elevation = 10.dp,
            modifier = Modifier.clip(CircleShape),
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color(selectedColor).copy(alpha = 0.6f),
            border = BorderStroke(10.dp, Color(selectedColor))
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select a color:",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Canvas(
                    modifier = Modifier.clip(RoundedCornerShape(25.dp))
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val cellSize = size.width / COLORS.size
                                val index = (offset.x / cellSize).toInt()
                                if (index in COLORS.indices) {
                                    selectedColor = COLORS[index].toInt()
                                }
                            }
                        }
                ) {
                    val cellSize = size.width / COLORS.size
                    COLORS.forEachIndexed { index, color ->
                        drawCircle(
                            color = Color(color),
                            radius = cellSize / 2,
                            center = Offset((index + 0.5f) * cellSize, size.height / 2)
                        )
                    }
                    val selectedColorIndex = COLORS.indexOf(selectedColor.toLong())
                    if (selectedColorIndex != -1) {
                        val centerX = (selectedColorIndex + 0.5f) * cellSize
                        val centerY = size.height / 2
                        val radius = cellSize / 2 * 0.8f
                        drawCircle(
                            color = Color.White,
                            radius = radius + 4.dp.toPx(),
                            center = Offset(centerX, centerY),
                            style = Fill
                        )
                        drawCircle(
                            color = Color(selectedColor),
                            radius = radius,
                            center = Offset(centerX, centerY),
                            style = Fill
                        )
                        drawCircle(
                            color = Color.Black,
                            radius = radius,
                            center = Offset(centerX, centerY),
                            style = Stroke(2f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(shape = RoundedCornerShape(20.dp),onClick = {
                    onDismiss()
                    onColorSelected(selectedColor)
                    updatecolor()
                }) {
                    Text(text = "Save Your Color", color = Color.White)
                }
            }
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