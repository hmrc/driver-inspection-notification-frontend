#!/bin/bash

echo "Running git checkout"

projectDir=driver-inspection-notification-frontend
if [ -z $gitCloneRef ]
then
  gitCloneRef=git@github.com:hmrc/driver-inspection-notification-frontend.git
  echo "[gitCloneRef] environment variable is empty, defaulting to: $gitCloneRef"
else
  echo "[gitCloneRef] environment variable is set to: $gitCloneRef"
fi
gitCommitRef=main

now=$(date +"%T")

currentEnglishMessages="current_messages"
currentWelshMessages="current_messages.cy"
currentBulgarianMessages="current_messages.bg"
currentCroatianMessages="current_messages.hr"
currentCzechMessages="current_messages.cs"
currentFrenchMessages="current_messages.fr"
currentGermanMessages="current_messages.de"
currentHungarianMessages="current_messages.hu"
currentLithuanianMessages="current_messages.lt"
currentRomanianMessages="current_messages.ro"
currentPolishMessages="current_messages.pl"
currentSpanishMessages="current_messages.es"

tempDir='translation_temp'

translationDir=${PWD}/target/scala-2.13/content-classes
cd ..
hmrcPath=${PWD}
tempPath=$hmrcPath/$tempDir
projectPath=$tempPath/$projectDir

echo hmrcPath is $hmrcPath
echo projectPath is $projectPath
echo tempPath is $tempPath
echo projecDir is $projectDir
echo translationDir is $translationDir

cd $hmrcPath || return

echo Creating $tempDir ...
if [ -e $tempDir ]
then
    mv $tempDir $tempDir.$now
fi

mkdir $tempDir
cd $tempDir || return

echo Cloning $gitCloneRef may take a moment ...
git clone $gitCloneRef
cd $projectDir || return
git checkout $gitCommitRef

echo Copying current English messages ...
cp "$projectPath/conf/messages"    "$tempPath/$currentEnglishMessages"

echo Copying current Welsh messages ...
cp "$projectPath/conf/messages.cy" "$tempPath/$currentWelshMessages"

echo Copying current Bulgarian messages ...
cp "$projectPath/conf/messages.bg" "$tempPath/$currentBulgarianMessages"

echo Copying current Croatian messages ...
cp "$projectPath/conf/messages.hr" "$tempPath/$currentCroatianMessages"

echo Copying current Czech messages ...
cp "$projectPath/conf/messages.cs" "$tempPath/$currentCzechMessages"

echo Copying current French messages ...
cp "$projectPath/conf/messages.fr" "$tempPath/$currentFrenchMessages"

echo Copying current German messages ...
cp "$projectPath/conf/messages.de" "$tempPath/$currentGermanMessages"

echo Copying current Hungarian messages ...
cp "$projectPath/conf/messages.hu" "$tempPath/$currentHungarianMessages"

echo Copying current lithuanian messages ...
cp "$projectPath/conf/messages.lt" "$tempPath/$currentLithuanianMessages"

echo Copying current Romanian messages ...
cp "$projectPath/conf/messages.ro" "$tempPath/$currentRomanianMessages"

echo Copying current Polish messages ...
cp "$projectPath/conf/messages.pl" "$tempPath/$currentPolishMessages"

echo Copying current Spanish messages ...
cp "$projectPath/conf/messages.es" "$tempPath/$currentSpanishMessages"

echo moving files ....
mv "$tempPath/$currentEnglishMessages" "$translationDir"
mv "$tempPath/$currentWelshMessages" "$translationDir"
mv "$tempPath/$currentBulgarianMessages" "$translationDir"
mv "$tempPath/$currentCroatianMessages" "$translationDir"
mv "$tempPath/$currentCzechMessages" "$translationDir"
mv "$tempPath/$currentFrenchMessages" "$translationDir"
mv "$tempPath/$currentGermanMessages" "$translationDir"
mv "$tempPath/$currentHungarianMessages" "$translationDir"
mv "$tempPath/$currentLithuanianMessages" "$translationDir"
mv "$tempPath/$currentRomanianMessages" "$translationDir"
mv "$tempPath/$currentPolishMessages" "$translationDir"
mv "$tempPath/$currentSpanishMessages" "$translationDir"

echo Tidying up ...
rm -rf $tempPath
