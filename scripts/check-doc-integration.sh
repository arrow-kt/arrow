#!/bin/bash

BRANCH=master
if [ $# -eq 1 ]; then BRANCH=$1; fi

. $BASEDIR/arrow/scripts/commons4gradle.sh
updateOrchestrator $BRANCH
. $BASEDIR/arrow/scripts/commons4gradle.sh
. $BASEDIR/arrow/scripts/commons4filesystem.sh

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle

$BASEDIR/arrow/scripts/site-download.sh
runAndSaveResult "Site" "Run Ank" "$BASEDIR/arrow/scripts/site-run-ank.sh"

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do
    checkAndDownloadViaHTTPS $repository $BRANCH

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties

    runAndSaveResult $repository "Local install" "$BASEDIR/arrow/scripts/project-install.sh $repository"
    runAndSaveResult $repository "Undo local changes" "$BASEDIR/arrow/scripts/project-undo-local-changes.sh $repository"
done

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do
    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties
    if [ -f $BASEDIR/$repository/arrow-docs/build.gradle ]; then
        replaceOSSbyLocalRepository $BASEDIR/$repository/arrow-docs/build.gradle
    fi
    addArrowDocs $BASEDIR/$repository/settings.gradle
    runAndSaveResult $repository "Generate and validate doc" "$BASEDIR/arrow/scripts/project-generate-and-validate-doc.sh $repository"
done

#runAndSaveResult "Site" "Prepare env" "$BASEDIR/arrow/scripts/site-prepare-env.sh"
#runAndSaveResult "Site" "Site build" "$BASEDIR/arrow/scripts/site-build.sh"

showFiles
exitForResult
