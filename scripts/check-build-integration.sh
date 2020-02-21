#!/bin/bash

set -ex
export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/..
. ./scripts/commons.sh
export BASEDIR=$(pwd)

replaceOSSbyLocalRepository generic-conf.gradle
removeAnkFromCommonBuild doc-conf.gradle

for repository in $(cat lists/build.txt); do
    git clone https://github.com/arrow-kt/$repository.git

    replaceGlobalPropertiesbyLocalConf $repository/gradle.properties

    removeAnkFromCustomBuild $repository/build.gradle
    for module in $repository/arrow-*; do
        removeAnkFromCustomBuild $module/build.gradle
    done
    replaceOSSbyLocalRepository $repository/build.gradle

    ./scripts/project-install.sh $repository
done

for repository in $(cat lists/build.txt); do
    ./scripts/project-simple-build.sh $repository
done
