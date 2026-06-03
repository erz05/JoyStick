package com.erz.joystick.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erz.joysticklibrary.JoystickDirection
import com.erz.joysticklibrary.JoystickType

@Composable
fun HudPanel(
    moveDirectionProvider: () -> JoystickDirection,
    moveAngleProvider: () -> Double,
    movePowerProvider: () -> Double,
    rotationDirectionProvider: () -> JoystickDirection,
    rotationAngleProvider: () -> Float,
    rotationPowerProvider: () -> Double,
    selectedJoystickType: JoystickType,
    selectedJoystickTypeRight: JoystickType,
    stayPutLeft: Boolean,
    stayPutRight: Boolean,
    onTypeSelected: (JoystickType) -> Unit,
    onTypeSelectedRight: (JoystickType) -> Unit,
    onStayPutChanged: (Boolean) -> Unit,
    onStayPutChangedRight: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val fontScale = density.fontScale
    val portraitScreenWidth = minOf(configuration.screenWidthDp, configuration.screenHeightDp)
    val panelWidth = (portraitScreenWidth * 0.9f).dp

    // Calculate non-scalable font sizes based on panel width percentage to prevent layout breaking
    val titleFontSize = ((panelWidth.value * 0.031f) / fontScale).sp
    val bodyFontSize = ((panelWidth.value * 0.028f) / fontScale).sp
    val labelFontSize = ((panelWidth.value * 0.025f) / fontScale).sp

    var isExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .width(panelWidth)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x331E2433))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Interactive Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "JOYSTICK CONTROLS COMPOSABLE DEMO",
                color = Color(0xFF03DAC6),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = if (isExpanded) "▲" else "▼",
                color = Color(0xFF03DAC6),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                // Telemetry Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Joy HUD
                    Column {
                        Text(
                            text = "LEFT: MOVEMENT",
                            color = Color.White,
                            fontSize = titleFontSize,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Dir: ${moveDirectionProvider()}",
                            color = Color.Gray,
                            fontSize = bodyFontSize,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Angle: ${String.format("%.1f", Math.toDegrees(moveAngleProvider()))}°",
                            color = Color.Gray,
                            fontSize = bodyFontSize,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Power: ${String.format("%.1f", movePowerProvider())}%",
                            color = Color.Gray,
                            fontSize = bodyFontSize,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Right Joy HUD
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "RIGHT: ROTATION",
                            color = Color.White,
                            fontSize = titleFontSize,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Dir: ${rotationDirectionProvider()}",
                            color = Color.Gray,
                            fontSize = bodyFontSize,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Rotation: ${String.format("%.1f", rotationAngleProvider())}°",
                            color = Color.Gray,
                            fontSize = bodyFontSize,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Power: ${String.format("%.1f", rotationPowerProvider())}%",
                            color = Color.Gray,
                            fontSize = bodyFontSize,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Divider line using Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x22FFFFFF))
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Settings Section (Left/Right Axis Mode Dropdowns + StayPut L Checkbox underneath)
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left Axis Mode Dropdown
                        Column {
                            Text(
                                text = "LEFT AXIS MODE",
                                color = Color.White,
                                fontSize = bodyFontSize,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            var dropdownExpandedLeft by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { dropdownExpandedLeft = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    val currentText = selectedJoystickType.name
                                        .replace("_AXIS", "")
                                        .replace("TWO_AXIS_", "2-Axis ")
                                        .replace("FOUR_", "4-Axis")
                                        .replace("EIGHT_", "8-Axis")
                                    Text(
                                        text = "$currentText ▼",
                                        fontSize = titleFontSize,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                DropdownMenu(
                                    expanded = dropdownExpandedLeft,
                                    onDismissRequest = { dropdownExpandedLeft = false },
                                    modifier = Modifier.background(Color(0xFF1E2433))
                                ) {
                                    JoystickType.entries.forEach { type ->
                                        val isSelected = selectedJoystickType == type
                                        val typeText = type.name
                                            .replace("_AXIS", "")
                                            .replace("TWO_AXIS_", "2-Axis ")
                                            .replace("FOUR_", "4-Axis")
                                            .replace("EIGHT_", "8-Axis")
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = typeText,
                                                    color = if (isSelected) Color(0xFF03DAC6) else Color.White,
                                                    fontSize = titleFontSize,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                onTypeSelected(type)
                                                dropdownExpandedLeft = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Right Axis Mode Dropdown
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "RIGHT AXIS MODE",
                                color = Color.White,
                                fontSize = bodyFontSize,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            var dropdownExpandedRight by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { dropdownExpandedRight = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    val currentText = selectedJoystickTypeRight.name
                                        .replace("_AXIS", "")
                                        .replace("TWO_AXIS_", "2-Axis ")
                                        .replace("FOUR_", "4-Axis")
                                        .replace("EIGHT_", "8-Axis")
                                    Text(
                                        text = "$currentText ▼",
                                        fontSize = titleFontSize,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                DropdownMenu(
                                    expanded = dropdownExpandedRight,
                                    onDismissRequest = { dropdownExpandedRight = false },
                                    modifier = Modifier.background(Color(0xFF1E2433))
                                ) {
                                    JoystickType.entries.forEach { type ->
                                        val isSelected = selectedJoystickTypeRight == type
                                        val typeText = type.name
                                            .replace("_AXIS", "")
                                            .replace("TWO_AXIS_", "2-Axis ")
                                            .replace("FOUR_", "4-Axis")
                                            .replace("EIGHT_", "8-Axis")
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = typeText,
                                                    color = if (isSelected) Color(0xFF03DAC6) else Color.White,
                                                    fontSize = titleFontSize,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                onTypeSelectedRight(type)
                                                dropdownExpandedRight = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left StayPut Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = stayPutLeft,
                                onCheckedChange = onStayPutChanged,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF03DAC6)
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "StayPut L",
                                color = Color.White,
                                fontSize = labelFontSize
                            )
                        }

                        // Right StayPut Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = stayPutRight,
                                onCheckedChange = onStayPutChangedRight,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF03DAC6)
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "StayPut R",
                                color = Color.White,
                                fontSize = labelFontSize
                            )
                        }
                    }
                }
            }
        }
    }
}
