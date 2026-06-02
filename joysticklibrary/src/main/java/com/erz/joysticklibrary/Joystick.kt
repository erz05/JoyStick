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
        private val valuesByValue = entries.associateBy { it.value }
        fun fromValue(value: Int): JoystickDirection = valuesByValue[value] ?: CENTER
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
    val currentOnMove by rememberUpdatedState(onMove)
    val currentOnTap by rememberUpdatedState(onTap)
    val currentOnDoubleTap by rememberUpdatedState(onDoubleTap)

    val scale = remember(radiusScale) { radiusScale.coerceIn(0.25f, 0.50f) }

    var thumbOffsetX by remember { mutableFloatStateOf(0f) }
    var thumbOffsetY by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(stayPut, type, scale) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val minDim = min(size.width, size.height).toFloat()
                val maxTravelDistance = (minDim / 2f) * (1f - scale)

                if (maxTravelDistance <= 0f) return@pointerInput

                val touchSlop = viewConfiguration.touchSlop
                var lastTapTime = 0L

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val now = System.currentTimeMillis()
                    val isDoubleTap = now - lastTapTime < 300L

                    var dragTriggered = false
                    val pointerId = down.id

                    fun updatePosition(rawX: Float, rawY: Float) {
                        val deltaX = rawX - centerX
                        val deltaY = rawY - centerY

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

                        val newOffsetX: Float
                        val newOffsetY: Float
                        if (distance > maxTravelDistance) {
                            newOffsetX = targetDx * maxTravelDistance / distance
                            newOffsetY = targetDy * maxTravelDistance / distance
                        } else {
                            newOffsetX = targetDx
                            newOffsetY = targetDy
                        }

                        // Skip callback if thumb position hasn't meaningfully changed
                        if (newOffsetX == thumbOffsetX && newOffsetY == thumbOffsetY) return

                        thumbOffsetX = newOffsetX
                        thumbOffsetY = newOffsetY

                        val power = (100 * sqrt(thumbOffsetX * thumbOffsetX + thumbOffsetY * thumbOffsetY) / maxTravelDistance).toDouble()
                        val angle = atan2(-thumbOffsetY.toDouble(), -thumbOffsetX.toDouble())
                        val direction = calculateDirection(Math.toDegrees(angle))

                        currentOnMove(angle, power, direction)
                    }

                    updatePosition(down.position.x, down.position.y)

                    while (true) {
                        val event = awaitPointerEvent()

                        val change = event.changes.firstOrNull { it.id == pointerId }

                        if (change != null && change.positionChanged()) {
                            // Only count as drag if movement exceeds touch slop
                            if (!dragTriggered) {
                                val totalDragX = change.position.x - down.position.x
                                val totalDragY = change.position.y - down.position.y
                                val totalDrag = sqrt(totalDragX * totalDragX + totalDragY * totalDragY)
                                if (totalDrag > touchSlop) {
                                    dragTriggered = true
                                }
                            }
                            updatePosition(change.position.x, change.position.y)
                            change.consume()
                        }

                        if (event.changes.all { !it.pressed }) {
                            if (!dragTriggered) {
                                if (isDoubleTap) {
                                    currentOnDoubleTap?.invoke()
                                    lastTapTime = 0L // Reset to prevent triple-tap as double
                                } else {
                                    currentOnTap?.invoke()
                                    lastTapTime = now // Only record tap time for actual taps
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

        // Draw Button tracking states cleanly
        val posX = centerX + thumbOffsetX
        val posY = centerY + thumbOffsetY

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

// Clean, O(1) mathematical lookup mapping degrees flawlessly to standard directions
private fun calculateDirection(degrees: Double): JoystickDirection {
    // Normalize degrees from [-180, 180] to [0, 360)
    val normalized = if (degrees < 0) degrees + 360.0 else degrees

    // Shift by 22.5 degrees so that the "LEFT" sector spans across the 0/360 boundary cleanly
    val shifted = (normalized + 22.5) % 360.0

    // Map 45-degree chunks to their respective indices
    return when ((shifted / 45.0).toInt()) {
        0 -> JoystickDirection.LEFT
        1 -> JoystickDirection.LEFT_UP
        2 -> JoystickDirection.UP
        3 -> JoystickDirection.UP_RIGHT
        4 -> JoystickDirection.RIGHT
        5 -> JoystickDirection.RIGHT_DOWN
        6 -> JoystickDirection.DOWN
        7 -> JoystickDirection.DOWN_LEFT
        else -> JoystickDirection.CENTER
    }
}