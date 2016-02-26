#!/bin/bash

set -e

ADB=/code/android/SDK/platform-tools/adb
REMOTE=hayesp@bitter.cs.berkeley.edu
LOGCAT="BIDMach_Android:* System.out:* AndroidRuntime:* JOCL:* *:S"

ssh $REMOTE << EOF
    echo adb logcat $LOGCAT
    $ADB logcat $LOGCAT
EOF

exit
