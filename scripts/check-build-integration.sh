#!/bin/bash

sleep 3m
BRANCH=master
if [ $# -eq 1 ]; then
    BRANCH=$1
fi
export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh
. $BASEDIR/arrow/scripts/commons4filesystem.sh

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do
    checkAndDownloadViaHTTPS $repository $BRANCH

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties

    runAndSaveResult $repository "Local install" "$BASEDIR/arrow/scripts/project-install.sh $repository"
done

for repository in $(cat $BASEDIR/arrow/lists/test.txt); do
    checkAndDownloadViaHTTPS $repository $BRANCH

    runAndSaveResult $repository "Test" "$BASEDIR/arrow/scripts/project-test.sh $repository"
done

showFiles
exitForResult
