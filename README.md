# JoyStick

An Android Library for JoyStick View & Composable.
Customizable, small, lightweight, and modern.

[![Download](https://api.bintray.com/packages/erz05/maven/JoyStick/images/download.svg)](https://bintray.com/erz05/maven/JoyStick/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-JoyStick-blue.svg?style=flat)](http://android-arsenal.com/details/1/2712)

---

## 🚀 Modernization & v2.0.0 (BREAKING CHANGE)

This library has been completely modernized to Kotlin and Jetpack Compose:
1. **Written in Kotlin**: All source code is converted to Kotlin.
2. **Jetpack Compose Support**: Introduced the new `Joystick` Composable function.
3. **Legacy View Deprecation**: The legacy View-based `JoyStick` class is deprecated, but remains available in Kotlin for backward compatibility.
4. **Modern Build System**: Upgraded to Gradle 9.1.0, AGP 9.0.1, Kotlin 2.3.20, and uses Version Catalogs (`libs.versions.toml`).
5. **Increased Min SDK**: The minimum SDK has been bumped to **21** to support Jetpack Compose.

---

## 🎨 Jetpack Compose Usage

The new `Joystick` composable is highly customizable, supports custom sizing, custom color styling, and custom images via Compose Painters.

### Basic Setup
```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erz.joysticklibrary.Joystick
import com.erz.joysticklibrary.JoystickDirection

var angle by remember { mutableStateOf(0.0) }
var power by remember { mutableStateOf(0.0) }
var direction by remember { mutableStateOf(JoystickDirection.CENTER) }

Joystick(
    modifier = Modifier.size(150.dp),
    onMove = { newAngle, newPower, newDirection ->
        angle = newAngle
        power = newPower
        direction = newDirection
    }
)
```

### Advanced Customs & Painters
```kotlin
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

Joystick(
    modifier = Modifier.size(200.dp),
    type = JoystickType.FOUR_AXIS,          // Snap to 4 directions
    stayPut = true,                         // Button stays where dragged
    radiusScale = 0.30f,                    // Scale button size (0.25f - 0.50f)
    
    // Sized by Colors
    padColor = Color(0x33FFFFFF),
    buttonColor = Color.Red,
    
    // OR Customize with Painters (Bitmaps or Vector drawables)
    padPainter = painterResource(id = R.drawable.custom_pad),
    buttonPainter = painterResource(id = R.drawable.custom_button),
    
    onMove = { angle, power, direction -> 
        /* Handle movement */ 
    },
    onTap = { 
        /* Handle tap event */ 
    },
    onDoubleTap = { 
        /* Handle double tap event */ 
    }
)
```

### Axis Types (`JoystickType`)
- `JoystickType.EIGHT_AXIS` (Default, 8 directions)
- `JoystickType.FOUR_AXIS` (4 directions: Left, Up, Right, Down)
- `JoystickType.TWO_AXIS_LEFT_RIGHT` (Restricted to Left/Right)
- `JoystickType.TWO_AXIS_UP_DOWN` (Restricted to Up/Down)

### Directions (`JoystickDirection`)
- `JoystickDirection.CENTER` (-1)
- `JoystickDirection.LEFT` (0)
- `JoystickDirection.LEFT_UP` (1)
- `JoystickDirection.UP` (2)
- `JoystickDirection.UP_RIGHT` (3)
- `JoystickDirection.RIGHT` (4)
- `JoystickDirection.RIGHT_DOWN` (5)
- `JoystickDirection.DOWN` (6)
- `JoystickDirection.DOWN_LEFT` (7)

---

## ⚠️ Legacy View Usage (DEPRECATED)

The old XML/View-based custom view `JoyStick` is deprecated. If you are migrating a legacy project, you can continue to use it. It has been converted to Kotlin:

```xml
<com.erz.joysticklibrary.JoyStick
    android:id="@+id/joyStick"
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:padColor="#55ffffff"
    app:buttonColor="#55ff0000"
    app:stayPut="true"
    app:percentage="25"
    app:backgroundDrawable="@drawable/pad"
    app:buttonDrawable="@drawable/button"/>
```

In your Activity/Fragment:
```kotlin
val joyStick = findViewById<JoyStick>(R.id.joyStick)
joyStick.setListener(object : JoyStick.JoyStickListener {
    override fun onMove(joyStick: JoyStick?, angle: Double, power: Double, direction: Int) {
        // Handle move
    }
    override fun onTap() {}
    override fun onDoubleTap() {}
})
```

---

## 📦 Installation

To include the library in your Gradle project:

### Version Catalog (`libs.versions.toml`)
```toml
[libraries]
joystick = { module = "com.github.erz05:JoyStick", version = "2.0.0" }
```

### Module Build Script (`build.gradle.kts`)
```kotlin
dependencies {
    implementation(libs.joystick)
    // Or if importing as a local project module:
    // implementation(project(":joysticklibrary"))
}
```

---

## 📄 License

    Copyright 2015 erz05

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
