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
    selectedJoystickTypeRight: JoystickType,
    stayPutLeft: Boolean,
    stayPutRight: Boolean,
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
        // ---------------------------------------------------------------------------------
        // LEFT JOYSTICK: Droid Character Movement Control
        //
        // This joystick demonstrates how to customize the visual appearance using solid Colors
        // and configure dynamic settings (JoystickType and stayPut):
        //
        // 1. Modifier.size(160.dp) shapes the boundary area of the joystick.
        // 2. clip(RoundedCornerShape(80.dp)) ensures touch gesture inputs are clipped/bounded.
        // 3. type = selectedJoystickType changes restriction constraints dynamically (e.g. 2-axis, 4-axis, 8-axis).
        // 4. stayPut = stayPutLeft controls whether the button snaps back or stays at the offset when released.
        // 5. padColor & buttonColor provide solid customized backgrounds instead of using drawables.
        // 6. onMove updates the viewmodel coordinates (angle, power, direction) to move the droid.
        // ---------------------------------------------------------------------------------
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

        // ---------------------------------------------------------------------------------
        // RIGHT JOYSTICK: Droid Rotation & Projectile Control
        //
        // This joystick demonstrates usage of custom image assets (Painters) for both the
        // background pad and front button thumb, along with tap and double-tap gestures:
        //
        // 1. type = selectedJoystickTypeRight configures axis restrictions dynamically.
        // 2. stayPut = stayPutRight controls whether the button snaps back or stays at the offset.
        // 3. padPainter & buttonPainter load SVG/XML custom assets to style the controls.
        // 4. onMove updates the rotation angle in degrees, allowing the droid to face the control angle.
        // 5. onTap handles single click events to fire a single ball.
        // 6. onDoubleTap handles rapid double-tap events to execute a coroutine-based 5-ball rapid fire sequence.
        // ---------------------------------------------------------------------------------
        Joystick(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color(0x1AFFFFFF))
                .border(2.dp, Color(0x33FFFFFF), RoundedCornerShape(80.dp)),
            type = selectedJoystickTypeRight,
            stayPut = stayPutRight,
            padPainter = painterResource(id = R.drawable.pad),
            buttonPainter = painterResource(id = R.drawable.button),
            onMove = onMoveRight,
            onTap = onTapRight,
            onDoubleTap = onDoubleTapRight
        )
    }
}
