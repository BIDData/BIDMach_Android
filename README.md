# BIDMach_Android

## Current Status:
* BIDMach and BIDMat libraries are succuessfully imported, and the Android app written in Scala can be compiled and deployed.
* We are able to monitor outputs from the app.
* We are able to transfer the data files to the android device and have the app access it.
* The RCV1 dataset appears to be too big to be loaded, or at least it did not finish loading in half an hour. Memory usage appears to be normal. This is the part that will be worked on next. 
* If we reduce the size of the datasets, we can get them to load successfully.
* GLM learner works! (at least with the smaller datasets)
* OpenCL working (at least with some Adreno GPU SDK samples)
* Can now run custom OpenCL kernels through BIDMat

## Next Steps:
* Build wrapper library for OpenCL in BIDMat
* Start converting the ds_mult and sd_mult CUDA functions to OpenCL

## Use Instructions

1. Install Scala, sbt, the Android SDK, the Android NDK, and the Adreno GPU SDK.
2. Build both BIDMat and BIDMach with `sbt package`, and then copy over BIDMat.jar and BIDMach.jar to the app/libs/ folder.
3. Copy libOpenCL.so from your device (usually /vendor/lib/libOpenCL.so) to (*bidmat_path_whatever*/lib/libOpenCL.so)
2. In BIDMat, run ./build_android_native.sh (this will build the OpenCL native code and copy it into this folder for testing. make sure this folder is called BIDMat_Android in the same directory as BIDMat)
3. To run the sample android app written in Scala:
5. cd to `app/`
6. Connect Android device to computer. Verify connection by typing `adb devices` and see your device id come up.
7. Run `adb -d logcat BIDMach_Android:* HELLO_CL:* System.out:* AndroidRuntime:* *:S` to view all messages from println
8. Run `sbt android:run` to deploy and run app to device

Note: Please make sure that `platformTarget in Android` in `build.sbt` is set to the correct Android API level of the device/emulator. 

## Useful Commands:

* Run `adb shell` to open a remote shell on the android device. 

The following commands assume you're in the device shell already. This is not required, as one can simply issue `adb shell <cmd>` to run shell commands as well. 

* Run `am start -n com.BIDMach/.MainActivity` to run the app again
* Run `pm uninstall com.BIDMach` to uninstall the app.
* Run `vmstat` to see current memory and cpu stats
