### Setting up Snapdragon Dev Boards (similar for many Android phones)

###Setting Paths to use ADB etc. 
You need a copy of ant installed somewhere. Adjust the path below (/code/ant/bin) to the location of Ant's bin directory. 
The following variables should be set in your .bashrc (including on Windows/Cygwin):

<pre>
export ANDROID_SDK=/c/Android/sdk
export ANDROID_NDK=/c/Android/sdk/ndk-bundle
export PATH=$PATH:$ANDROID_SDK/tools:$ANDROID_SDK/platform-tools:$ANDROID_NDK:/code/ant/bin
</pre>
This will put the SDK and NDK tools in your path. 

#### Setting up the Windows Driver
Windows requires a USB driver to use ADB to a devboard or phone. The devkits dont normally have hardware profiles in the driver, 
so you have to edit the in inf file. 

First, install the Google driver from the Android SDK Manager. Its under "SDK Tools". Once installed, you can find the file in:
${ANDROID_SDK}/extras/google/usb_driver/android_winusb.inf Under &#91;Google.NTamd64&#93; (if you're on 64bit windows, else &#91;Google.NT86&#93;).
Create entries for the hardware ids of the USB device like this:

<pre>
&#91;Google.NTamd64&#93;

;Inforce 6540
%SingleAdbInterface%        = USB_Install, USB\VID_05C6&PID_9025&MI_00
%CompositeAdbInterface%     = USB_Install, USB\VID_05C6&PID_9025&REV_????&MI_00
%SingleAdbInterface%        = USB_Install, USB\VID_05C6&PID_9025&MI_01
%CompositeAdbInterface%     = USB_Install, USB\VID_05C6&PID_9025&REV_????&MI_01
%SingleAdbInterface%        = USB_Install, USB\VID_05C6&PID_9025&MI_02
%CompositeAdbInterface%     = USB_Install, USB\VID_05C6&PID_9025&REV_????&MI_02
%SingleAdbInterface%        = USB_Install, USB\VID_05C6&PID_9025&MI_03
%CompositeAdbInterface%     = USB_Install, USB\VID_05C6&PID_9025&REV_????&MI_03
%SingleAdbInterface%        = USB_Install, USB\VID_05C6&PID_9025&MI_04
%CompositeAdbInterface%     = USB_Install, USB\VID_05C6&PID_9025&REV_????&MI_04
</pre>

You can find the hardware address in Windows Device Manager. The Android board will show up as several devices with missing/bad 
drivers. Right click on them, select "Properties" then "Details". Set the "Property" pulldown to "Hardware Ids" and the Ids 
will show up. Copy them into the driver inf. file.

Once the driver inf file has been modified, go back to each of the ailing devices in device manager. Right click on each one, 
select "Update Driver Software" and "Browse my computer for driver software". Browse to ${ANDROID_SDK}/extras/google/usb_driver,
and click "Next". The driver should Install. 

Check by running
<pre>
adb devices
</pre>

You should see something like:
<pre> 
List of devices attached
f00e0587        device
</pre>
Note that the Qualcomm dev boards dont automatically start on power-up, you have to hit and hold the power on button. 

###Accessing the Filesystem on an Adroid Device

The filesystem is read-only by default. To be able to write to it, you should first switch to root mode, and then remount the filesystem read-write. You can do this with
<pre>
adb root
adb remount
</pre>
adb 

###Install QSML and Symphony

QSML is a cross-hardware math library that implements BLAS. It currently runs on CPU only, but should eventually use the GPU. 
Symphony is a runtime that support polymorphic hardware. You should download and install both tools from the Qualcomm dev site. First Symphony. Assume you installed it in ${SYMPHONY_DIR}, then on your host machine do:

<pre> 
cd ${SYMPHONY_DIR}
ls
</pre>
and you will see the supported architectures. `arm-linux-androideabi` is 32bit ARM Android (Android API 21 and earlier). `aarch64-linux-android` is for 64bit ARM builds, (Android API 22 and later). Note that 32-bit applications should work on 64bit
machines. Assuming you have a 32bit Android, do 
<pre>
cd arm-linux-androideabi/lib
adb push libsymphony.so /vendor/lib/libsymphony.so
adb push libsymphony-1.0.0.so /vendor/lib/libsymphony-1.0.0.so
adb push libsymphony-cpu-1.0.0.so /vendor/lib/libsymphony-cpu-1.0.0.so
adb push libsymphony-gpu-1.0.0.so /vendor/lib/libsymphony-gpu-1.0.0.so
</pre>



