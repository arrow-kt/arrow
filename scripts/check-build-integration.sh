#!/bin/bash

export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh
. $BASEDIR/arrow/scripts/commons4filesystem.sh

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do
    if [ ! -d $BASEDIR/$repository ]; then
        cd $BASEDIR
        git clone https://github.com/arrow-kt/$repository.git
    fi

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties
    removeArrowDocs $BASEDIR/$repository/settings.gradle

    runAndSaveResult $repository "Local install" "$BASEDIR/arrow/scripts/project-install.sh $repository"
done

for repository in $(cat $BASEDIR/arrow/lists/test.txt); do
    if [ ! -d $BASEDIR/$repository ]; then
        cd $BASEDIR
        git clone https://github.com/arrow-kt/$repository.git
    fi
    runAndSaveResult $repository "Test" "$BASEDIR/arrow/scripts/project-test.sh $repository"
done

showFiles
exitForResult
