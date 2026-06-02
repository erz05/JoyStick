package com.erz.joysticklibrary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

enum class JoystickDirection(val value: Int) {
    CENTER(-1),
    LEFT(0),
    LEFT_UP(1),
    UP(2),
    UP_RIGHT(3),
    RIGHT(4),
    RIGHT_DOWN(5),
    DOWN(6),
    DOWN_LEFT(7);

    companion object {
        fun fromValue(value: Int): JoystickDirection {
            return entries.firstOrNull { it.value == value } ?: CENTER
        }
    }
}

enum class JoystickType(val value: Int) {
    EIGHT_AXIS(11),
    FOUR_AXIS(22),
    TWO_AXIS_LEFT_RIGHT(33),
    TWO_AXIS_UP_DOWN(44)
}

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    type: JoystickType = JoystickType.EIGHT_AXIS,
    stayPut: Boolean = false,
    radiusScale: Float = 0.25f,
    padColor: Color = Color.White,
    buttonColor: Color = Color.Red,
    padPainter: Painter? = null,
    buttonPainter: Painter? = null,
    onMove: (angle: Double, power: Double, direction: JoystickDirection) -> Unit = { _, _, _ -> },
    onTap: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null
) {
    // 1. Stabilize callbacks to prevent pointerInput from restarting
    val currentOnMove by rememberUpdatedState(onMove)
    val currentOnTap by rememberUpdatedState(onTap)
    val currentOnDoubleTap by rememberUpdatedState(onDoubleTap)

    val scale = radiusScale.coerceIn(0.25f, 0.50f)

    // 2. Use unboxed primitives and store relative offsets instead of absolute positions
    var thumbOffsetX by remember { mutableFloatStateOf(0f) }
    var thumbOffsetY by remember { mutableFloatStateOf(0f) }

    // 3. Remove Box and attach pointerInput directly to Canvas to reduce node depth
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(stayPut, type, scale) {
                // Read size directly from PointerInputScope
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val minDim = min(size.width, size.height).toFloat()
                val maxTravelDistance = (minDim / 2f) * (1f - scale)

                if (maxTravelDistance <= 0f) return@pointerInput

                var lastTapTime = 0L

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val now = System.currentTimeMillis()
                    val isDoubleTap = now - lastTapTime < 300L
                    lastTapTime = now

                    var dragTriggered = false
                    val pointerId = down.id

                    fun updatePosition(offset: Offset) {
                        val deltaX = offset.x - centerX
                        val deltaY = offset.y - centerY

                        var targetDx = deltaX
                        var targetDy = deltaY

                        when (type) {
                            JoystickType.TWO_AXIS_LEFT_RIGHT -> targetDy = 0f
                            JoystickType.TWO_AXIS_UP_DOWN -> targetDx = 0f
                            JoystickType.FOUR_AXIS -> {
                                if (abs(deltaX) > abs(deltaY)) targetDy = 0f else targetDx = 0f
                            }
                            JoystickType.EIGHT_AXIS -> { /* No restriction */ }
                        }

                        val distance = sqrt(targetDx * targetDx + targetDy * targetDy)

                        if (distance > maxTravelDistance) {
                            thumbOffsetX = targetDx * maxTravelDistance / distance
                            thumbOffsetY = targetDy * maxTravelDistance / distance
                        } else {
                            thumbOffsetX = targetDx
                            thumbOffsetY = targetDy
                        }

                        val power = (100 * sqrt(thumbOffsetX * thumbOffsetX + thumbOffsetY * thumbOffsetY) / maxTravelDistance).toDouble()

                        // Preserving original math logic by negating offsets
                        val angle = atan2(-thumbOffsetY.toDouble(), -thumbOffsetX.toDouble())
                        val direction = calculateDirection(Math.toDegrees(angle))

                        currentOnMove(angle, power, direction)
                    }

                    updatePosition(down.position)

                    while (true) {
                        val event = awaitPointerEvent()
                        val anyPositionChange = event.changes.any { it.positionChanged() }
                        if (anyPositionChange) {
                            dragTriggered = true
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: event.changes.first()
                            updatePosition(change.position)
                            change.consume()
                        }
                        if (event.changes.all { !it.pressed }) {
                            if (!dragTriggered) {
                                if (isDoubleTap) {
                                    currentOnDoubleTap?.invoke()
                                } else {
                                    currentOnTap?.invoke()
                                }
                            }
                            if (!stayPut) {
                                thumbOffsetX = 0f
                                thumbOffsetY = 0f
                                currentOnMove(0.0, 0.0, JoystickDirection.CENTER)
                            }
                            break
                        }
                    }
                }
            }
    ) {
        // Read size directly from DrawScope
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val minDim = min(size.width, size.height)
        val maxTravelDistance = (minDim / 2f) * (1f - scale)
        val buttonRadius = (minDim / 2f) * scale

        if (centerX == 0f || centerY == 0f) return@Canvas

        // Draw Background Pad
        if (padPainter != null) {
            translate(left = centerX - maxTravelDistance, top = centerY - maxTravelDistance) {
                with(padPainter) {
                    draw(size = Size(maxTravelDistance * 2, maxTravelDistance * 2))
                }
            }
        } else {
            drawCircle(
                color = padColor,
                radius = maxTravelDistance,
                center = Offset(centerX, centerY)
            )
        }

        // Calculate absolute position on the fly during Draw Phase
        val posX = centerX + thumbOffsetX
        val posY = centerY + thumbOffsetY

        // Draw Button
        if (buttonPainter != null) {
            translate(left = posX - buttonRadius, top = posY - buttonRadius) {
                with(buttonPainter) {
                    draw(size = Size(buttonRadius * 2, buttonRadius * 2))
                }
            }
        } else {
            drawCircle(
                color = buttonColor,
                radius = buttonRadius,
                center = Offset(posX, posY)
            )
        }
    }
}

private fun calculateDirection(degrees: Double): JoystickDirection {
    return when {
        (degrees >= 0 && degrees < 22.5) || (degrees < 0 && degrees > -22.5) -> JoystickDirection.LEFT
        degrees >= 22.5 && degrees < 67.5 -> JoystickDirection.LEFT_UP
        degrees >= 67.5 && degrees < 112.5 -> JoystickDirection.UP
        degrees >= 112.5 && degrees < 157.5 -> JoystickDirection.UP_RIGHT
        (degrees >= 157.5 && degrees <= 180) || (degrees >= -180 && degrees < -157.5) -> JoystickDirection.RIGHT
        degrees >= -157.5 && degrees < -112.5 -> JoystickDirection.RIGHT_DOWN
        degrees >= -112.5 && degrees < -67.5 -> JoystickDirection.DOWN
        degrees >= -67.5 && degrees < -22.5 -> JoystickDirection.DOWN_LEFT
        else -> JoystickDirection.CENTER
    }
}