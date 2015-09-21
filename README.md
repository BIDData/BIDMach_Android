# BIDMach_Android

##Current Status:
* BIDMach and BIDMat libraries are succuessfully imported, and the Android app written in Scala can be compiled and deployed.
* We are able to monitor outputs (println) from the app (see instructions below).
* We are able to transfer the data files to the android device and have the app access it.
* The RCV1 dataset appears to be too big to be loaded, or at least it did not finish loading in half an hour. Memory usage appears to be normal. This is the part that will be worked on next. 

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

Useful Commands:

* Run `adb shell` to open shell to android device. 

The following commands assume you're in shell already. This is not required, as one can simply issue `adb shell <cmd>` to run shell commands as well. 

* Run `am start -n com.BIDMach/.MainActivity` to run the app again
* Run `pm uninstall com.BIDMach` to uninstall a the app.
* Run `vmstat` to see current memory and cpu stats