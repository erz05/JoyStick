# JoyStick
Android Library for JoyStick View.<br>
Customizable, small and lightweight.

[![Download](https://api.bintray.com/packages/erz05/maven/JoyStick/images/download.svg)](https://bintray.com/erz05/maven/JoyStick/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-JoyStick-blue.svg?style=flat)](http://android-arsenal.com/details/1/2712)

<H2>Sample App</H2>
<img height="70px" src="https://github.com/erz05/JoyStick/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" />
<a href="https://play.google.com/store/apps/details?id=com.erz.joystick&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1"><img height="70px" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" /></a>

<img width="300px" src="https://github.com/erz05/JoyStick/blob/master/images/Screenshot_2015-10-30-21-38-13.png" />
<br><br>
<img width="300px" src="https://github.com/erz05/JoyStick/blob/master/images/Screenshot_2015-10-30-21-43-47.png" />
<br><br>
JoyStick with Image for button<br>
<img width="300px" src="https://github.com/erz05/JoyStick/blob/master/images/Screenshot_2015-11-02-18-05-49.png" />
<br>

<H2>Usage</H2>
Gradle Import: jcenter <br>

```groovy

repositories {
    maven {
        url  "http://dl.bintray.com/erz05/maven" 
    }
    
    //Or
    
    jcenter()
}

dependencies {
    compile 'com.github.erz05:JoyStick:1.1.0'
}
```

<H2>v1.1.0 BREAKING CHANGE!</H2>

1. Made changes to JoyStickListener<br>
a. Added Direction to onMove<br>
b. Added Event calls for onTap and onDoubleTap<br>

<H2>Defaults:</H2>

1. Background = White
2. Button = Red
3. Button Radius = 25%
4. StayPut = false
5. Directional-Axis = 8

<H2>Setup:</H2>

```xml
<com.erz.joysticklibrary.JoyStick
  android:id="@+id/joy1"
  android:layout_width="200dp"
  android:layout_height="200dp"
  android:layout_gravity="bottom"/>

<com.erz.joysticklibrary.JoyStick
    android:id="@+id/joy2"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:layout_gravity="bottom|right"
    app:padColor="#55ffffff"
    app:buttonColor="#55ff0000"
    app:stayPut="true"
    app:percentage="25" //default 25: radius percentage of full size of the view between 25% and 50%
    app:backgroundDrawable="R.drawable.background"
    app:buttonDrawable="R.drawable.button"/>
```

```java
JoyStick joyStick = (JoyStick) findViewById(R.id.joyStick);

//or 

JoyStick joyStick = new JoyStick(context);
```

<H2>JoyStickListener:</H2>

```java
//JoyStickListener Interface
public interface JoyStickListener {
        void onMove(JoyStick joyStick, double angle, double power, int direction);
        void onTap();
        void onDoubleTap();
}

//Set JoyStickListener
joyStick.setListener(this);
```
1. onMove: gets called everytime theres a touch interaction
2. onTap: gets called onSingleTapConfirmed
3. onDoubleTap: gets called onDoubleTap

<H2>Directions:</H2>
1. DIRECTION_CENTER = -1
2. DIRECTION_LEFT = 0
3. DIRECTION_LEFT_UP = 1
4. DIRECTION_UP = 2
5. DIRECTION_UP_RIGHT = 3 
6. DIRECTION_RIGHT = 4
7. DIRECTION_RIGHT_DOWN = 5 
8. DIRECTION_DOWN = 6
9. DIRECTION_DOWN_LEFT = 7

To get JoyStick direction you can use

```java
joyStick.getDirection();
```
or get it from the JoyStickListener

<H2>Axis Types:</H2>
1. TYPE_8_AXIS 
2. TYPE_4_AXIS 
3. TYPE_2_AXIS_LEFT_RIGHT 
4. TYPE_2_AXIS_UP_DOWN

To set Axis Type:

```java
joyStick.setType(JoyStick.TYPE_4_AXIS);
```

<H2>Getters/Setters</H2>

```java
//Set GamePad Color
joyStick.setPadColor(Color.BLACK);

//Set Button Color
joyStick.setButtonColor(Color.RED);

//Set Background Image
joyStick.setPadBackground(resId);

//Set Button Image
joyStick.setButtonDrawable(resId);

//Set Button Scale
joyStick.setButtonRadiusScale(scale);

//Enable Button to Stay Put
joyStick.enableStayPut(enable);

//Get Power
joyStick.getPower();

//Get Angle
joyStick.getAngle();

//Get Angle in Degrees
joyStick.getAngleDegrees();
```

<H2>License</H2>
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
