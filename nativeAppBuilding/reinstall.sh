#!/bin/bash
NA_CONVERTER=./src/handle_project.py
PACKAGE=at.tugraz.ist.catroid
ACTIVITY=app_1

CATROID_SRC=../catroid/
LIB_SRC=../libraryProjects/
TMP_DIR=./tmp

REMOVE_TMP=false
INSTALL_APK=true

PROJECT_FILE=$1
APK_FILENAME="${PROJECT_FILE%.*}.apk"

if [ -d $TMP_DIR ]; then
  if $REMOVE_TMP; then
    rm  $TMP_DIR
  else
    echo "Skipping temporary directory removal"
  fi
else
  mkdir $TMP_DIR
fi


#Uninstall old app
echo "Uninstalling App"
adb uninstall $PACKAGE.$ACTIVITY

#Delete catroid dir on sdcard
echo "Removing cartroid dir on sdcard"
adb shell rm -r /sdcard/catroid > /dev/null

if [ $# -eq 1 ]; then
  echo "Converting"
  python2 $NA_CONVERTER -v -n $1 $CATROID_SRC $LIB_SRC 1 $TMP_DIR
  
  if $INSTALL_APK ; then
    echo "Installing specified app"
    adb install tmp/$APK_FILENAME 
    echo "Removing generated apk file"
    rm tmp/$APK_FILENAME
  fi
fi

