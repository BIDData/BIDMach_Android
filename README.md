# BIDMach_Android

## Current Status:
* BIDMach and BIDMat libraries are succuessfully imported, and the Android app written in Scala can be compiled and deployed.
* We are able to monitor outputs from the app.
* We are able to transfer the data files to the android device and have the app access it.
* The RCV1 dataset appears to be too big to be loaded, or at least it did not finish loading in half an hour. Memory usage appears to be normal. This is the part that will be worked on next. 
* If we reduce the size of the datasets, we can get them to load successfully.
* GLM learner works! (at least with the smaller datasets)
* OpenCL working (at least with some Adreno GPU SDK samples)

## Next Steps:
* Verify data results on Android are consitent with typical desktop results.
* Determine more robust process for running OpenCL kernels on Android
* Start converting the ds_mult and sd_mult CUDA functions to OpenCL
* Runtime conditional binding to OpenCL and CUDA

## Use Instructions

Build both BIDMat and BIDMach with `sbt package`, and then copy over BIDMat.jar and BIDMach.jar to the app/libs/ folder.

Generate smaller, uncompressed versions of the sample data:

    # In the BIDMach directory, with data files downloaded
    $ ./getdata.sh
    $ ./bidmach

    val docs = loadSMat("./data/rcv1/docs.smat.lz4")
    val feature_count = sum(docs > 0, 2)
    val indices = find(feature_count > 10)
    val sdocs = docs(indices, 0 -> 4000)
    saveSMat("../BIDMach_Android/sdocs.smat");

    val cats = loadFMat("./data/rcv1/cats.fmat.lz4")
    val scats = cats(?, 0 -> 4000)
    saveFMat("../BIDMach_Android/scats.fmat")

To copy the training files:

1. Make sure adb has root access first by running `adb root`.
2. Use `adb push [FILENAME] [TARGET DIRECTORY]` or a 3rd party tool that can copy files to an android system.
3. Currently the app expects `sdocs.smat` and `scats.fmat` to be under `/mnt/sdcard/bidmach_data/`
4. Note, we don't use the compressed versions, as the lz4 decompressor is not currently working on Android.

To run the sample android app written in Scala:

1. Install sbt and Android SDK. Make sure sbt and adb are accessible from terminal.
2. cd to `app/`
3. Connect Android device to computer. Verify connection by typing `adb devices`
4. Run `adb -d logcat BIDMach_Android:* System.out:* AndroidRuntime:* *:S` to view all messages from println
5. Run `sbt android:run` to deploy and run app to device

Note: Please make sure that `platformTarget in Android` in `build.sbt` is set to the correct Android API level of the device/emulator. 

## Useful Commands:

* Run `adb shell` to open a remote shell on the android device. 

The following commands assume you're in shell already. This is not required, as one can simply issue `adb shell <cmd>` to run shell commands as well. 

* Run `am start -n com.BIDMach/.MainActivity` to run the app again
* Run `pm uninstall com.BIDMach` to uninstall the app.
* Run `vmstat` to see current memory and cpu stats
