# BIDMach_Android
To run the sample android app written in Scala:
1. Install sbt and Android SDK. Make sure sbt and adb are accessible from terminal.
2. Cd to app/
3. Connect Android device to computer. Verify connection by `adb devices`
4. Run `adb -d logcat System.out:I *:S` to view all messages from println
5. Run `sbt run` to deploy and run app to device
6. Run `adb shell am start -n com.BIDMach/.MainActivity` to run the app again

To uninstall the deployed package: `adb shell pm uninstall com.BIDMach`