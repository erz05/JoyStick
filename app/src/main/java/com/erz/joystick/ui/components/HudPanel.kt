package com.erz.joystick.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import com.erz.joysticklibrary.JoystickDirection

@Composable
fun HudPanel(
    moveDirectionProvider: () -> JoystickDirection,
    moveAngleProvider: () -> Double,
    movePowerProvider: () -> Double,
    rotationDirectionProvider: () -> JoystickDirection,
    rotationAngleProvider: () -> Float,
    rotationPowerProvider: () -> Double,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val portraitScreenWidth = minOf(configuration.screenWidthDp, configuration.screenHeightDp)
    val panelWidth = (portraitScreenWidth * 0.9f).dp

    Column(
        modifier = modifier
            .width(panelWidth)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x331E2433))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "JOYSTICK CONTROLS COMPOSABLE DEMO",
            color = Color(0xFF03DAC6),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Joy HUD
            Column {
                Text(
                    text = "LEFT: MOVEMENT",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dir: ${moveDirectionProvider()}",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Angle: ${String.format("%.1f", Math.toDegrees(moveAngleProvider()))}°",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Power: ${String.format("%.1f", movePowerProvider())}%",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Right Joy HUD
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "RIGHT: ROTATION",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dir: ${rotationDirectionProvider()}",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Rotation: ${String.format("%.1f", rotationAngleProvider())}°",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Power: ${String.format("%.1f", rotationPowerProvider())}%",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
