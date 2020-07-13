#!/bin/bash

ARROW_LIB=$1
BRANCH=master
if [ $# -eq 2 ]; then BRANCH=$2; fi

. $BASEDIR/arrow/scripts/commons4gradle.sh
updateOrchestrator $BRANCH
. $BASEDIR/arrow/scripts/commons4gradle.sh

addLocalRepositoryBeforeOSS $BASEDIR/arrow/generic-conf.gradle

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do

    if [ $repository != $ARROW_LIB ]; then
        checkAndDownloadViaHTTPS $repository $BRANCH

        if [ $BRANCH != "master" ]; then
            cd $BASEDIR/$repository
            FOUND_BRANCH=$(lookForBranchInPullRequests $BRANCH)
            if [ "$FOUND_BRANCH" != $BRANCH ] && [[ ! "$FOUND_BRANCH" =~ ":$BRANCH"$ ]]; then
                echo "Skipping $repository"
                continue
            fi
        fi
    fi

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties
    $BASEDIR/arrow/scripts/project-install.sh $repository
done
