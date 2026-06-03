package com.erz.joystick.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.erz.joystick.R

@Composable
fun DroidCharacter(
    droidSize: Dp,
    droidRadius: Float,
    xProvider: () -> Float,
    yProvider: () -> Float,
    rotationProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.droid),
        contentDescription = "Android Droid",
        modifier = modifier
            .size(droidSize)
            .offset {
                IntOffset(
                    (xProvider() - droidRadius).toInt(),
                    (yProvider() - droidRadius).toInt()
                )
            }
            .graphicsLayer {
                rotationZ = rotationProvider()
            }
    )
}
