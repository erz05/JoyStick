package com.erz.joystick.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.erz.joystick.R
import com.erz.joysticklibrary.Joystick
import com.erz.joysticklibrary.JoystickDirection
import com.erz.joysticklibrary.JoystickType

@Composable
fun JoystickControls(
    selectedJoystickType: JoystickType,
    stayPutLeft: Boolean,
    onMoveLeft: (Double, Double, JoystickDirection) -> Unit,
    onMoveRight: (Double, Double, JoystickDirection) -> Unit,
    onTapRight: () -> Unit,
    onDoubleTapRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Joystick (Movement, using customized colors)
        Joystick(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color(0x1AFFFFFF))
                .border(2.dp, Color(0x33FFFFFF), RoundedCornerShape(80.dp)),
            type = selectedJoystickType,
            stayPut = stayPutLeft,
            padColor = Color(0x334E5D78),
            buttonColor = Color(0x88BB86FC),
            onMove = onMoveLeft
        )

        // Right Joystick (Rotation, using drawable painters)
        Joystick(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color(0x1AFFFFFF))
                .border(2.dp, Color(0x33FFFFFF), RoundedCornerShape(80.dp)),
            stayPut = true, // Right joystick stays put
            padPainter = painterResource(id = R.drawable.pad),
            buttonPainter = painterResource(id = R.drawable.button),
            onMove = onMoveRight,
            onTap = onTapRight,
            onDoubleTap = onDoubleTapRight
        )
    }
}
