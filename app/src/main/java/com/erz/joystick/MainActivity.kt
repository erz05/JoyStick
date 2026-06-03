package com.erz.joystick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erz.joysticklibrary.Joystick
import com.erz.joysticklibrary.JoystickDirection
import com.erz.joysticklibrary.JoystickType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFFBB86FC),
                    secondary = Color(0xFF03DAC6),
                    background = Color(0xFF0F1219),
                    surface = Color(0xFF1E2433)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JoystickDemoScreen()
                }
            }
        }
    }
}

class ComposeStar(
    var x: Float,
    var y: Float,
    val speed: Float,
    val radius: Float
)

@Composable
fun JoystickDemoScreen() {
    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

    val stars = remember { mutableStateListOf<ComposeStar>() }

    // Droid state
    var droidX by remember { mutableStateOf(0f) }
    var droidY by remember { mutableStateOf(0f) }
    var droidRotation by remember { mutableStateOf(0f) }
    val droidSize = 72.dp
    val density = LocalDensity.current
    val droidSizePx = remember(density) { with(density) { droidSize.toPx() } }
    val droidRadius = droidSizePx / 2f

    // Joystick states
    var moveAngle by remember { mutableStateOf(0.0) }
    var movePower by remember { mutableStateOf(0.0) }
    var moveDirection by remember { mutableStateOf(JoystickDirection.CENTER) }

    var rotationAngle by remember { mutableStateOf(0.0) }
    var rotationPower by remember { mutableStateOf(0.0) }
    var rotationDirection by remember { mutableStateOf(JoystickDirection.CENTER) }

    // Settings
    var selectedJoystickType by remember { mutableStateOf(JoystickType.EIGHT_AXIS) }
    var stayPutLeft by remember { mutableStateOf(false) }

    // LaunchedEffect for the Game Loop
    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth <= 0f || screenHeight <= 0f) return@LaunchedEffect

        // Initialize stars
        if (stars.isEmpty()) {
            repeat(60) {
                stars.add(
                    ComposeStar(
                        x = Random.nextFloat() * screenWidth,
                        y = Random.nextFloat() * screenHeight,
                        speed = Random.nextFloat() * 6f + 2f,
                        radius = Random.nextFloat() * 3f + 1f
                    )
                )
            }
        }

        // Initialize droid position to center
        if (droidX == 0f && droidY == 0f) {
            droidX = screenWidth / 2f
            droidY = screenHeight / 2f
        }

        while (true) {
            withFrameMillis {
                // Update stars position
                stars.forEach { star ->
                    star.y += star.speed
                    if (star.y > screenHeight) {
                        star.y = 0f
                        star.x = Random.nextFloat() * screenWidth
                    }
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
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                screenWidth = it.width.toFloat()
                screenHeight = it.height.toFloat()
            }
    ) {
        // 1. Starfield Background Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw deep space background gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0D14), Color(0xFF141926))
                )
            )

            // Draw stars
            stars.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = Random.nextFloat() * 0.3f + 0.7f),
                    radius = star.radius,
                    center = Offset(star.x, star.y)
                )
            }
        }

        // 2. Android Droid Character
        if (droidX > 0f && droidY > 0f) {
            Image(
                painter = painterResource(id = R.drawable.droid),
                contentDescription = "Android Droid",
                modifier = Modifier
                    .size(droidSize)
                    .offset {
                        IntOffset(
                            (droidX - droidRadius).toInt(),
                            (droidY - droidRadius).toInt()
                        )
                    }
                    .graphicsLayer(
                        rotationZ = droidRotation
                    )
            )
        }

        // 3. HUD Display Card (Glassmorphic)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp)
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x331E2433))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "JOYSTICK CONTROLS COMPOSABLE DEMO",
                color = Color(0xFF03DAC6),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Joy HUD
                Column {
                    Text(
                        text = "LEFT: MOVEMENT",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dir: $moveDirection",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Angle: ${String.format("%.1f", Math.toDegrees(moveAngle))}°",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Power: ${String.format("%.1f", movePower)}%",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Right Joy HUD
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RIGHT: ROTATION",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dir: $rotationDirection",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Rotation: ${String.format("%.1f", droidRotation)}°",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Power: ${String.format("%.1f", rotationPower)}%",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 4. Middle settings card
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x44000000))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Axis Mode",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            JoystickType.entries.forEach { type ->
                val isSelected = selectedJoystickType == type
                Button(
                    onClick = { selectedJoystickType = type },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF6200EE) else Color(0x33FFFFFF)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = type.name.replace("_AXIS", "").replace("TWO_AXIS_", "2X_").replace("FOUR_", "4X").replace("EIGHT_", "8X"),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = stayPutLeft,
                    onCheckedChange = { stayPutLeft = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF03DAC6)
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "StayPut L",
                    color = Color.White,
                    fontSize = 9.sp
                )
            }
        }

        // 5. Controls Overlay (Bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
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
                onMove = { angle, power, direction ->
                    moveAngle = angle
                    movePower = power
                    moveDirection = direction
                }
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
                onMove = { angle, power, direction ->
                    rotationAngle = angle
                    rotationPower = power
                    rotationDirection = direction
                }
            )
        }
    }
}
