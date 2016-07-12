#!/bin/bash

set -e

REMOTE=hayesp@bitter.cs.berkeley.edu
DEVICE=e178dbfe
ADB="/code/android/SDK/platform-tools/adb -s $DEVICE"
LOGCAT="BIDMach_Android:* System.out:* AndroidRuntime:* JOCL:* *:S"

echo ssh $REMOTE
ssh $REMOTE << EOF
    echo adb logcat $LOGCAT
    $ADB logcat $LOGCAT
EOF

exit
