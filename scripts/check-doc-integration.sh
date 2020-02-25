#!/bin/bash

set -ex
export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. ./arrow/scripts/commons.sh

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle

$BASEDIR/arrow/scripts/site-download.sh
$BASEDIR/arrow/scripts/site-run-ank.sh

for repository in $(cat $BASEDIR/arrow/lists/build.txt); do
    if [ ! -d $BASEDIR/$repository ]; then
        cd $BASEDIR
        git clone https://github.com/arrow-kt/$repository.git
    fi

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties
    removeArrowDocs $BASEDIR/$repository/settings.gradle

    $BASEDIR/arrow/scripts/project-install.sh $repository
    $BASEDIR/arrow/scripts/project-undo-local-changes.sh $repository
done

for repository in $(cat $BASEDIR/arrow/lists/build.txt); do
    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties
    if [ -f $BASEDIR/$repository/arrow-docs/build.gradle ]; then
        replaceOSSbyLocalRepository $BASEDIR/$repository/arrow-docs/build.gradle
    fi
done

echo "Run Dokka and Ank ..."
for repository in $(cat $BASEDIR/arrow/lists/build.txt); do
    $BASEDIR/arrow/scripts/project-run-dokka.sh $repository
    $BASEDIR/arrow/scripts/project-run-ank.sh $repository
    $BASEDIR/arrow/scripts/project-locate-doc.sh $repository
done

$BASEDIR/arrow/scripts/site-prepare-env.sh
$BASEDIR/arrow/scripts/site-build.sh
