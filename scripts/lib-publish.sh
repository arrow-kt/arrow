#!/bin/bash

set -e
. $BASEDIR/arrow/scripts/commons4gradle.sh

ARROW_LIB=$1

replaceOSSbyLocalRepository "$BASEDIR/arrow/gradle/*.gradle"

for lib in $(cat $BASEDIR/arrow/lists/libs.txt); do
    checkAndDownloadBranch $lib master
    replaceGlobalPropertiesbyLocalConf $BASEDIR/$lib/gradle.properties
    $BASEDIR/arrow/scripts/project-install.sh $lib
done
$BASEDIR/arrow/scripts/project-publish.sh $ARROW_LIB
