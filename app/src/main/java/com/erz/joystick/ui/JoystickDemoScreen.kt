package com.erz.joystick.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erz.joystick.ui.components.*
import com.erz.joystick.utils.HapticManager
import com.erz.joystick.viewmodel.JoystickDemoViewModel
import com.erz.joystick.viewmodel.JoystickUiEvent

@Composable
fun JoystickDemoScreen(
    viewModel: JoystickDemoViewModel = viewModel()
) {
    val context = LocalContext.current
    val hapticManager = remember(context) { HapticManager(context) }

    // Screen-level sizes
    val density = LocalDensity.current
    val droidSize = 72.dp
    val droidSizePx = remember(density) { with(density) { droidSize.toPx() } }
    val droidRadius = droidSizePx / 2f
    val ballRadiusPx = remember(density) { with(density) { 6.dp.toPx() } }

    // Observe side-effects from ViewModel (Haptics)
    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is JoystickUiEvent.TriggerHaptic -> {
                    hapticManager.triggerFeedback(event.isDoubleTap)
                }
            }
        }
    }

    // Ticker Loop for Starfield & Movement Simulation
    var frameTime by remember { mutableStateOf(0L) }
    LaunchedEffect(viewModel.screenWidth, viewModel.screenHeight) {
        if (viewModel.screenWidth <= 0f || viewModel.screenHeight <= 0f) return@LaunchedEffect
        
        while (true) {
            withFrameMillis { time ->
                frameTime = time
                viewModel.tick()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                viewModel.updateScreenSize(
                    width = it.width.toFloat(),
                    height = it.height.toFloat(),
                    droidRadius = droidRadius,
                    ballRadius = ballRadiusPx
                )
            }
    ) {
        // 1. Canvas Background
        StarfieldBackground(
            starsProvider = { viewModel.stars },
            ballsProvider = { viewModel.balls },
            frameTimeProvider = { frameTime }
        )

        // 2. Droid Character (Deferred layout offsets)
        val isDroidActive = viewModel.droidX > 0f && viewModel.droidY > 0f
        if (isDroidActive) {
            DroidCharacter(
                droidSize = droidSize,
                droidRadius = droidRadius,
                xProvider = { viewModel.droidX },
                yProvider = { viewModel.droidY },
                rotationProvider = { viewModel.droidRotation }
            )
        }

        // 3. HUD Display Card (now includes Axis Mode and StayPut settings)
        HudPanel(
            moveDirectionProvider = { viewModel.moveDirection },
            moveAngleProvider = { viewModel.moveAngle },
            movePowerProvider = { viewModel.movePower },
            rotationDirectionProvider = { viewModel.rotationDirection },
            rotationAngleProvider = { viewModel.droidRotation },
            rotationPowerProvider = { viewModel.rotationPower },
            selectedJoystickType = viewModel.selectedJoystickType,
            selectedJoystickTypeRight = viewModel.selectedJoystickTypeRight,
            stayPutLeft = viewModel.stayPutLeft,
            stayPutRight = viewModel.stayPutRight,
            onTypeSelected = { viewModel.setJoystickType(it) },
            onTypeSelectedRight = { viewModel.setJoystickTypeRight(it) },
            onStayPutChanged = { viewModel.updateStayPutLeft(it) },
            onStayPutChangedRight = { viewModel.updateStayPutRight(it) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp)
        )

        // 5. Controls Overlay (Bottom Joysticks)
        JoystickControls(
            selectedJoystickType = viewModel.selectedJoystickType,
            selectedJoystickTypeRight = viewModel.selectedJoystickTypeRight,
            stayPutLeft = viewModel.stayPutLeft,
            stayPutRight = viewModel.stayPutRight,
            onMoveLeft = { angle, power, dir -> viewModel.onMoveLeft(angle, power, dir) },
            onMoveRight = { angle, power, dir -> viewModel.onMoveRight(angle, power, dir) },
            onTapRight = { viewModel.fireBall() },
            onDoubleTapRight = { viewModel.fireRapidBurst() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}
