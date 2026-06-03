package com.erz.joystick.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erz.joystick.domain.model.Ball
import com.erz.joystick.domain.model.ComposeStar
import com.erz.joysticklibrary.JoystickDirection
import com.erz.joysticklibrary.JoystickType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

sealed interface JoystickUiEvent {
    data class TriggerHaptic(val isDoubleTap: Boolean) : JoystickUiEvent
}

class JoystickDemoViewModel : ViewModel() {
    // Screen and bounds dimensions (density scaled from UI)
    var screenWidth = 0f
        private set
    var screenHeight = 0f
        private set
    var droidRadius = 0f
        private set
    var ballRadius = 0f
        private set

    // Configuration Settings
    var selectedJoystickType by mutableStateOf(JoystickType.EIGHT_AXIS)
        private set
    var stayPutLeft by mutableStateOf(false)
        private set

    // Left Joystick UI State
    var moveAngle by mutableStateOf(0.0)
        private set
    var movePower by mutableStateOf(0.0)
        private set
    var moveDirection by mutableStateOf(JoystickDirection.CENTER)
        private set

    // Right Joystick UI State
    var rotationAngle by mutableStateOf(0.0)
        private set
    var rotationPower by mutableStateOf(0.0)
        private set
    var rotationDirection by mutableStateOf(JoystickDirection.CENTER)
        private set

    // Simulation Entities
    val stars = mutableStateListOf<ComposeStar>()
    val balls = mutableStateListOf<Ball>()

    // Droid State
    var droidX by mutableStateOf(0f)
        private set
    var droidY by mutableStateOf(0f)
        private set
    var droidRotation by mutableStateOf(0f)
        private set

    // Event Flow for side-effects (e.g. Haptics)
    private val _eventChannel = Channel<JoystickUiEvent>(Channel.BUFFERED)
    val eventFlow = _eventChannel.receiveAsFlow()

    fun updateScreenSize(width: Float, height: Float, droidRadius: Float, ballRadius: Float) {
        screenWidth = width
        screenHeight = height
        this.droidRadius = droidRadius
        this.ballRadius = ballRadius
        
        // Initialize Droid to Center
        if (droidX == 0f && droidY == 0f && width > 0 && height > 0) {
            droidX = width / 2f
            droidY = height / 2f
        }
        
        // Initialize Stars
        if (stars.isEmpty() && width > 0 && height > 0) {
            repeat(60) {
                stars.add(
                    ComposeStar(
                        x = Random.nextFloat() * width,
                        y = Random.nextFloat() * height,
                        speed = Random.nextFloat() * 6f + 2f,
                        radius = Random.nextFloat() * 3f + 1f
                    )
                )
            }
        }
    }

    fun setJoystickType(type: JoystickType) {
        selectedJoystickType = type
    }

    fun updateStayPutLeft(stayPut: Boolean) {
        stayPutLeft = stayPut
    }

    fun onMoveLeft(angle: Double, power: Double, direction: JoystickDirection) {
        moveAngle = angle
        movePower = power
        moveDirection = direction
    }

    fun onMoveRight(angle: Double, power: Double, direction: JoystickDirection) {
        rotationAngle = angle
        rotationPower = power
        rotationDirection = direction
    }

    fun tick() {
        if (screenWidth <= 0f || screenHeight <= 0f) return

        // Update stars position
        stars.forEach { star ->
            star.y += star.speed
            if (star.y > screenHeight) {
                star.y = 0f
                star.x = Random.nextFloat() * screenWidth
            }
        }

        // Update balls position
        for (i in balls.indices) {
            val ball = balls.getOrNull(i) ?: continue
            ball.x += ball.vx
            ball.y += ball.vy
        }
        // Clean up off-screen balls
        balls.removeAll { ball ->
            ball.x < -50f || ball.x > screenWidth + 50f ||
            ball.y < -50f || ball.y > screenHeight + 50f
        }

        // Update droid position from left joystick
        if (movePower > 0) {
            val speedScale = 0.15f
            val dx = -cos(moveAngle).toFloat() * (movePower.toFloat() * speedScale)
            val dy = sin(-moveAngle).toFloat() * (movePower.toFloat() * speedScale)

            droidX = (droidX + dx).coerceIn(droidRadius, screenWidth - droidRadius)
            droidY = (droidY + dy).coerceIn(droidRadius, screenHeight - droidRadius)
        }

        // Update droid rotation from right joystick
        if (rotationPower > 0 && rotationAngle != 0.0) {
            droidRotation = (Math.toDegrees(rotationAngle).toFloat() - 90f)
        }
    }

    fun fireBall() {
        val rad = Math.toRadians((droidRotation.toDouble() - 90.0))
        val ballSpeed = 16f
        val vx = cos(rad).toFloat() * ballSpeed
        val vy = sin(rad).toFloat() * ballSpeed

        val startX = droidX + cos(rad).toFloat() * droidRadius
        val startY = droidY + sin(rad).toFloat() * droidRadius

        balls.add(
            Ball(
                x = startX,
                y = startY,
                vx = vx,
                vy = vy,
                radius = ballRadius,
                color = Color(0xFF03DAC6)
            )
        )
        viewModelScope.launch {
            _eventChannel.send(JoystickUiEvent.TriggerHaptic(isDoubleTap = false))
        }
    }

    fun fireRapidBurst() {
        viewModelScope.launch {
            repeat(5) { index ->
                fireBall()
                _eventChannel.send(JoystickUiEvent.TriggerHaptic(isDoubleTap = (index == 0)))
                delay(80)
            }
        }
    }
}
