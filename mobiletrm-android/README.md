# MobileTRM Android Jetpack Compose app

The Jetpack Compose app serves as native Android app for employees to consult the projects they work on and add new time registrations. <br/>
It communicates with the Quarkus back-end API.

## Prerequisites
* [Android Studio](https://developer.android.com/studio) Iguana or higher
* JDK 21 (from [Eclipse Temurin](https://adoptium.net/en-GB/temurin/releases/?package=jdk))

## Installing and running the application

### In Android Studio
First, make sure Gradle will build the app using Java's SDK version 21. <br/>
Navigate to ``Settings > Build, Execution, Deployment > Build Tools > Gradle``. <br/>
Under *Gradle SDK:*, make sure you've selected *Eclipse Temurin version 21*. <br/>
You can easily download and install this SDK by pressing *Download SDK..*. <br/>

Android Studio should have an Android emulator ready-to-use running the latest version of Android.
You can run Android Studio's run configuration _"app"_ to build the app and install it on the Android emulator.

### On a physical device
Alternatively, you can run the below script to create an APK to install on a physical Android device:
```shell script
./gradlew assembleDebug
```
> **Note**: You may need to enable running external APK's on your physical device. You can follow [this](https://www.androidauthority.com/how-to-install-apks-31494/) tutorial to enable installing third-party applications on your device.<br/>

You can find the APK you've just built [here](/app/build/outputs/apk/debug).

## Testing

### Unit and Integration tests
You can directly run tests from within Android Studio by navigating here:
* [Unit tests](/app/src/test)
* [Integration tests](/app/src/androidTest)

Right-click the test classes and press "Run".