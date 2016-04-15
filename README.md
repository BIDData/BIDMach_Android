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
* Back-fill omatcopy for QSML.
* Optimize ADAGrad with native implementation

## Next Steps:
* Integrate CLBlast + CLTune directly, to get full, performant BLAS functionality on OpenCL.
* Get [boda](https://github.com/moskewcz/boda) working
* Optimize convolution kernels
* Flesh out OpenCL matrix and ND array implementations

## Benchmarks

Some benchmarks have been done for Qualcomm 805 and 820 boards. You can find them in the `benchmarks/` folder.

## Setup

Install Scala, sbt, Maven, the Android SDK, and the Android NDK.

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
# install [android-cmake](https://github.com/taka-no-me/android-cmake)
# Linux:
cd /usr/share/cmake-3.2/Modules/
sudo wget https://raw.githubusercontent.com/taka-no-me/android-cmake/master/android.toolchain.cmake

cd <development-base-directory>
git clone git@github.com:phlip9/JOCL.git
git clone git@github.com:phlip9/JOCLCommon.git
cd JOCL

# Build the native OpenCL libraries for Android
mkdir build
cd build
# See [android-cmake] for extended parameter descriptions
cmake -DCMAKE_TOOLCHAIN_FILE=android.toolchain -DCMAKE_BUILD_TYPE=Release -DANDROID_ABI=armeabi-v7a -DANDROID_NATIVE_API_LEVEL=21 ..
cmake --build .
cd ..

# Build the .jar file
mvn clean install -DskipTests

# Copy the files into the correct locations
cp nativeLibraries/armeabi-v7a/libJOCL_0_2_0-android-arm.so ../BIDMach_Android/app/src/main/libs/armeabi-v7a/ 
cp jocl-0.2.0-RC01-SNAPSHOT.jar ../BIDMat/libs/
cp jocl-0.2.0-RC01-SNAPSHOT.jar ../BIDMach_Android/app/libs/

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
