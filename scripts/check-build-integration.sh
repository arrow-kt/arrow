#!/bin/bash

BRANCH=master
if [ $# -eq 1 ]; then BRANCH=$1; fi

. $BASEDIR/arrow/scripts/commons4gradle.sh
updateOrchestrator $BRANCH
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

    runAndSaveResult $repository "Check" "$BASEDIR/arrow/scripts/project-check.sh $repository"
done

showFiles
exitForResult
