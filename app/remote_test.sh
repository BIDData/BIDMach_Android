#!/bin/bash

set -e

APK_FILE=BIDMach_Android-debug.apk
APK_PATH=./target/android/output/$APK_FILE
PACKAGE=com.BIDMach
REMOTE=hayesp@bitter.cs.berkeley.edu
DEVICE=e178dbfe
ADB="/code/android/SDK/platform-tools/adb -s $DEVICE"

if [ ! -f $APK_PATH ]; then
    echo "Could not find APK=$APK_PATH"
    exit 1
fi

ssh $REMOTE << EOF
    echo mkdir -p ~/.tmp/
    mkdir -p ~/.tmp/
EOF

rsync -vz --progress $APK_PATH $REMOTE:~/.tmp/$APK_FILE

ssh $REMOTE << EOF
    echo adb install -r ~/.tmp/$APK_FILE
    $ADB install -r ~/.tmp/$APK_FILE
    echo adb shell am start -n $PACKAGE/.MainActivity
    $ADB shell am start -n $PACKAGE/.MainActivity
EOF

exit
