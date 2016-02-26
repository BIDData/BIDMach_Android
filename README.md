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
* JOCL wrapper library working
* OpenBLAS integrated. Back-filled BLAS routines for non-Intel machines.
* OpenCL SGEMM routine working and benchmarked. (~75 GFlops on Adreno 530, ~23 GFlops on Adreno 420)

## Next Steps:
* Integrate CLBlast + CLTune directly, to get full, performant BLAS functionality on OpenCL.
* Back-fill omatcopy for QSML.
* Optimize ADAGrad.

## Benchmarks

Benchmarks have been done for Qualcomm 805 and 820 boards. You can find them in the `benchmarks/` folder.

## Setup

Install Scala, sbt, the Android SDK, and the Android NDK.

#### Clone this repo

```bash
git clone git@github.com:BIDData/BIDMach_Android.git
```

#### Clone BIDMat

```bash
git clone git@github.com:BIDData/BIDMat.git
cd BIDMat
git checkout opencl
cd ..
```

#### Build JOCL (If using OpenCL)

```bash
git clone git@github.com:phlip9/JOCL.git
cd JOCL

# Build the native OpenCL libraries for android
mkdir build
cd build
# See cmake/android.toolchain.cmake for cmake flag details
cmake -DCMAKE_TOOLCHAIN_FILE=../cmake/android.toolchain.cmake -DCMAKE_BUILD_TYPE=Release -DANDROID_ABI=armeabi-v7a -DANDROID_NATIVE_API_LEVEL=19 ..
cmake --build .
cd ..

# Build the .jar file
sbt package

# Copy the files into the correct locations
cp libs/armeabi-v7a/libjocl.so ../BIDMach_Android/
cp jocl.jar ../BIDMat/libs/
cp jocl.jar ../BIDMach_Android/app/libs/

cd ..
```

#### Build BIDMat

```bash
cd BIDMat
# Fetch the dependencies
ARCH=linux-arm ./getdevlibs.sh

# Build the native libraries for BIDMat
cd jni/src
ndk-build clean
ndk-build
cp libs/armeabi-v7a/*.so ../../../BIDMach_Android/app/src/main/libs/armeabi-v7a/
 
cd ../..

# Build BIDMat.jar
sbt package
cp BIDMat.jar ../BIDMach_Android/app/libs/

cd ..
```

#### Running on Android

```bash
cd BIDMach_Android

sbt
> android:run   # run on the device
> android:package # build the .apk file (in target/android/output/)
> clean   # clean build files

# watch the ouput
adb logcat BIDMach_Android:* *:S

# see any errors
adb logcat art:* System.out:* AndroidRuntime:* *:S
```

#### Running on Android through a remote ssh server

```bash
sbt android:package
./remote_test.sh   # install the .apk on the remote device
./remote_log.sh    # view the remote device's logcat
```

Note: Make sure that `platformTarget in Android` in `build.sbt` is set to the correct Android API level for whatever device/emulator you're using.


## Useful Commands:

* Run `adb shell` to open a remote shell on the android device. 

The following commands assume you're in the device shell already. This is not required, as one can simply issue `adb shell <cmd>` to run shell commands as well. 

* Run `am start -n com.BIDMach/.MainActivity` to run the app again
* Run `pm uninstall com.BIDMach` to uninstall the app.
* Run `vmstat` to see current memory and cpu stats
