# BIDMach_Android

##Use Instructions
To copy the training files:

1. Make sure adb has root access first by running `adb root`.
2. Use `adb push [FILENAME] [TARGET DIRECTORY]` or a 3rd party tool that can copy files to an android system.
3. Currently the app expects `docs.smat.lz4` and `cats.fmat.lz4` to be under `data/BIDMach_Data/`, where data is under the root folder.

To run the sample android app written in Scala:

1. Install sbt and Android SDK. Make sure sbt and adb are accessible from terminal.
2. Cd to `app/`
3. Connect Android device to computer. Verify connection by `adb devices`
4. Run `adb -d logcat System.out:I *:S` to view all messages from println
5. Run `sbt run` to deploy and run app to device (on a separate terminal)
6. Run `adb shell am start -n com.BIDMach/.MainActivity` to run the app again

To uninstall the deployed package: `adb shell pm uninstall com.BIDMach`