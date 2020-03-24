#!/bin/bash

export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh
. $BASEDIR/arrow/scripts/commons4filesystem.sh

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle

$BASEDIR/arrow/scripts/site-download.sh
runAndSaveResult "Site" "Run Ank" "$BASEDIR/arrow/scripts/site-run-ank.sh"

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do
    if [ ! -d $BASEDIR/$repository ]; then
        git clone https://github.com/arrow-kt/$repository.git $BASEDIR/$repository
    fi

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
