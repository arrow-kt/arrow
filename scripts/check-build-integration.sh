#!/bin/bash

export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh
. $BASEDIR/arrow/scripts/commons4filesystem.sh

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do
    if [ ! -d $BASEDIR/$repository ]; then
        git clone https://github.com/arrow-kt/$repository.git $BASEDIR/$repository
    fi

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties

    runAndSaveResult $repository "Local install" "$BASEDIR/arrow/scripts/project-install.sh $repository"
done

for repository in $(cat $BASEDIR/arrow/lists/test.txt); do
    if [ ! -d $BASEDIR/$repository ]; then
        git clone https://github.com/arrow-kt/$repository.git $BASEDIR/$repository
    fi
    runAndSaveResult $repository "Test" "$BASEDIR/arrow/scripts/project-test.sh $repository"
done

showFiles
exitForResult
