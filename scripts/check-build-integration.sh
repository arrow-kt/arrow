#!/bin/bash

set -ex
export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. ./arrow/scripts/commons.sh

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle

for repository in $(cat $BASEDIR/arrow/lists/build.txt); do
    git clone https://github.com/arrow-kt/$repository.git

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$repository/gradle.properties
    removeArrowDocs $BASEDIR/$repository/settings.gradle

    $BASEDIR/arrow/scripts/project-install.sh $repository
done

for repository in $(cat $BASEDIR/arrow/lists/build.txt); do
    $BASEDIR/arrow/scripts/project-build.sh $repository
done
