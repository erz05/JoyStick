# JoyStick
Android Library for JoyStick View.<br>
Its customizable, small and lightweight.

[ ![Download](https://api.bintray.com/packages/erz05/maven/JoyStick/images/download.svg) ](https://bintray.com/erz05/maven/JoyStick/_latestVersion)

<H2>Images: Sample App</H2>
<img width="300px" src="https://github.com/erz05/JoyStick/blob/master/images/Screenshot_2015-10-30-21-38-13.png" />
<br>
<img width="300px" src="https://github.com/erz05/JoyStick/blob/master/images/Screenshot_2015-10-30-21-43-47.png" />
<br>

<H2>Usage</H2>
Gradle Import: jcenter <br>
```groovy

repositories {
    jcenter()
}

dependencies {
    compile 'com.github.erz05:JoyStick:1.0.0@aar'
}
```

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
    app:buttonColor="#55ff0000"/>
```

```java
JoyStick joyStick = (JoyStick) findViewById(R.id.joyStick);

//or 

JoyStick joyStick = new JoyStick(context);

//Set GamePad Color
joyStick.setPadColor(Color.BLACK);

//Set Button Color
joyStick.setButtonColor(Color.RED);

//Get Power
joyStick.getPower();

//Get Radians
joyStick.getRadians();

//Set JoyStickListener
joyStick.setListener(this);

//JoyStickListener Interface
public interface JoyStickListener {
        void onMove(double radians, double power);
}
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

