[![](https://jitpack.io/v/frhnfrq/PinView.svg)](https://jitpack.io/#frhnfrq/PinView)

# PinView 

A `PinView` library for Android. To enter pin/otp. After typing a number, focus automatically changes to next field. It supports backspace, focus can be changed to previous field by pressing backspace. Can be customized with custom fonts and background drawables.

<p float="left">
<img src="screenshots/1.png" width="270px" height="480px" />
<img src="screenshots/2.png" width="270px" height="480px" />
</p>

## Setup

Add it in your **root** build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add `implementation 'com.github.frhnfrq:PinView:1.0'` into **dependencies** section of your **module** build.gradle file. For example:

```groovy
dependencies {
    implementation 'com.github.frhnfrq:PinView:1.0'
}
```
## Usage

#### Add `PinView` in your layout

```xml
<xyz.farhanfarooqui.pinview.PinView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:pBackground="@drawable/background_pin"
        app:pBackgroundFilled="@drawable/background_pin_filled"
        app:pCount="6"
        app:pSize="35dp"
        app:pGap="5dp"
        app:pTextColorSelected="#000000"
        app:pTextColor="#FFFFFF"
        app:pTextSize="16sp"
        app:pFont="montserrat.otf"/>
```

#### Get an instance of it in your code
```java
PinView pinview = findViewById(R.id.pinview);
String pin = pinview.getPin();
if (pin == null) {
    // all fields are not filled
} else {
    // your code
}

// you can also manually set a pin
pinview.setPin("123456"); // the length must match pin count
```


## Attributes

* **pCount** : Length of your pin code.
* **pSize** : Height and width of each pin field.
* **pGap**, : Gap between each pin field.
* **pTextSize** : Text size of each pin.
* **pTextColor** : Text color of each pin.
* **pTextColorSelected**, Text color of the selected pin field.
* **pFont** : Name of the font file in your assets/fonts .directory (with extension).
* **pBackground** : Background drawable for pin field.
* **pBackgroundFilled** : Background drawable for pin field when it's filled.

## Custom Background Drawable examples

### pBackground
* **background_pin.xml**
```xml
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/background_pin_normal" android:state_focused="false" />
    <item android:drawable="@drawable/background_pin_focused" android:state_focused="true" />
    <item android:drawable="@drawable/background_pin_normal" />
</selector>
```

* **background_pin_normal.xml**
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#FFFFFF" />
    <corners android:radius="4dp" />
    <stroke
        android:width="1dp"
        android:color="#CFCFCF" />
</shape>
```

* **background_pin_focused.xml**
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#FFFFFF" />
    <corners android:radius="4dp" />
    <stroke
        android:width="1dp"
        android:color="#ce4242" />
</shape>
```
### pBackgroundFilled

* **background_pin_filled.xml**
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#ce4242" />
    <corners android:radius="4dp" />
</shape>
```


License
=======

   Copyright 2018 Farhan Farooqui

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

