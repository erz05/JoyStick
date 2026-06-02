package com.erz.joysticklibrary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
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
            return values().firstOrNull { it.value == value } ?: CENTER
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
    val scale = radiusScale.coerceIn(0.25f, 0.50f)

    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    val centerX = viewSize.width / 2f
    val centerY = viewSize.height / 2f
    val minDim = min(viewSize.width, viewSize.height)

    val buttonRadius = (minDim / 2f) * scale
    val maxTravelDistance = (minDim / 2f) * (1f - scale)

    var posX by remember(centerX) { mutableStateOf(centerX) }
    var posY by remember(centerY) { mutableStateOf(centerY) }

    // Update posX and posY when center changes due to layout size changes
    LaunchedEffect(centerX, centerY) {
        posX = centerX
        posY = centerY
    }

    Box(
        modifier = modifier
            .onSizeChanged { viewSize = it }
            .pointerInput(centerX, centerY, maxTravelDistance, stayPut, type, scale) {
                if (centerX == 0f || centerY == 0f || maxTravelDistance <= 0f) return@pointerInput

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

                        var targetX = offset.x
                        var targetY = offset.y

                        when (type) {
                            JoystickType.TWO_AXIS_LEFT_RIGHT -> {
                                targetY = centerY
                            }
                            JoystickType.TWO_AXIS_UP_DOWN -> {
                                targetX = centerX
                            }
                            JoystickType.FOUR_AXIS -> {
                                if (abs(deltaX) > abs(deltaY)) {
                                    targetY = centerY
                                } else {
                                    targetX = centerX
                                }
                            }
                            JoystickType.EIGHT_AXIS -> {
                                // No restriction
                            }
                        }

                        val dx = targetX - centerX
                        val dy = targetY - centerY
                        val distance = sqrt(dx * dx + dy * dy)

                        val clampedX: Float
                        val clampedY: Float
                        if (distance > maxTravelDistance) {
                            clampedX = (dx * maxTravelDistance / distance + centerX)
                            clampedY = (dy * maxTravelDistance / distance + centerY)
                        } else {
                            clampedX = targetX
                            clampedY = targetY
                        }

                        posX = clampedX
                        posY = clampedY

                        val power = (100 * sqrt(
                            (posX - centerX) * (posX - centerX) + (posY - centerY) * (posY - centerY)
                        ) / maxTravelDistance).toDouble()
                        val angle = atan2((centerY - posY).toDouble(), (centerX - posX).toDouble())
                        val direction = calculateDirection(Math.toDegrees(angle))

                        onMove(angle, power, direction)
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
                            // All pointers released
                            if (!dragTriggered) {
                                if (isDoubleTap) {
                                    onDoubleTap?.invoke()
                                } else {
                                    onTap?.invoke()
                                }
                            }
                            if (!stayPut) {
                                posX = centerX
                                posY = centerY
                                onMove(0.0, 0.0, JoystickDirection.CENTER)
                            }
                            break
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
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
