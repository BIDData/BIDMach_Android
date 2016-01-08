### Setting up Snapdragon Dev Boards (similar for many Android phones)

#### Setting up the Windows Driver
Windows requires a USB driver to use ADB to a devboard or phone. The devkits dont normally have hardware profiles in the driver, 
so you have to edit the in inf file. 

First, install the Google driver from the Android SDK Manager. Its under "SDK Tools". Once installed, you can find the file in:
${ANDROID_SDK}/extras/google/usb_driver/android_winusb.inf Under [Google.NTamd64] (if you're on 64bit windows, else [Google.NT86]).
Create entries for the hardware ids of the USB device like this:

<pre>
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
