package com.erz.joystick.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erz.joysticklibrary.JoystickType

@Composable
fun SettingsPanel(
    selectedJoystickType: JoystickType,
    stayPutLeft: Boolean,
    onTypeSelected: (JoystickType) -> Unit,
    onStayPutChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
                onClick = { onTypeSelected(type) },
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
                onCheckedChange = onStayPutChanged,
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
}
