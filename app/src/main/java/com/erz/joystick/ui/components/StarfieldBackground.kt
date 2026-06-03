package com.erz.joystick.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.erz.joystick.domain.model.Ball
import com.erz.joystick.domain.model.ComposeStar
import kotlin.random.Random

@Composable
fun StarfieldBackground(
    starsProvider: () -> List<ComposeStar>,
    ballsProvider: () -> List<Ball>,
    frameTimeProvider: () -> Long,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep space background gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0A0D14), Color(0xFF141926))
            )
        )

        // Draw stars
        starsProvider().forEach { star ->
            drawCircle(
                color = Color.White.copy(alpha = Random.nextFloat() * 0.3f + 0.7f),
                radius = star.radius,
                center = Offset(star.x, star.y)
            )
        }

        // Draw active projectiles (balls)
        val _tick = frameTimeProvider() // Subscribe to draw ticks
        ballsProvider().forEach { ball ->
            // Outer glow
            drawCircle(
                color = ball.color.copy(alpha = 0.3f),
                radius = ball.radius * 2f,
                center = Offset(ball.x, ball.y)
            )
            // Inner core
            drawCircle(
                color = ball.color,
                radius = ball.radius,
                center = Offset(ball.x, ball.y)
            )
        }
    }
}
