package com.erz.joystick.domain.model

import androidx.compose.ui.graphics.Color

data class Ball(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val color: Color = Color(0xFF03DAC6)
)
