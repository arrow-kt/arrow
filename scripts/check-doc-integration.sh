#!/bin/bash

set -ex
export JAVA_OPTS="-Xms512m -Xmx1024m"
cd $(dirname $0)/..
. ./scripts/commons.sh
export BASEDIR=$(pwd)

replaceOSSbyLocalRepository generic-conf.gradle
removeAnkFromCommonBuild doc-conf.gradle

./scripts/site-download.sh
./scripts/site-run-ank.sh

for repository in $(cat lists/build.txt); do
    git clone https://github.com/arrow-kt/$repository.git

    replaceGlobalPropertiesbyLocalConf $repository/gradle.properties

    removeAnkFromCustomBuild $repository/build.gradle
    replaceOSSbyLocalRepository $repository/build.gradle
    for module in $repository/arrow-*; do
        removeAnkFromCustomBuild $module/build.gradle
        replaceOSSbyLocalRepository $module/build.gradle
    done

    ./scripts/project-install.sh $repository
    ./scripts/project-undo-local-changes.sh $repository
done

git checkout $BASEDIR/doc-conf.gradle
for repository in $(cat lists/build.txt); do
    replaceGlobalPropertiesbyLocalConf $repository/gradle.properties
    replaceOSSbyLocalRepository $repository/build.gradle
    for module in $repository/arrow-*; do
        replaceOSSbyLocalRepository $module/build.gradle
    done
done

echo "Run Dokka and Ank ..."
for repository in $(cat lists/build.txt); do
    ./scripts/project-run-dokka.sh $repository
    ./scripts/project-run-ank.sh $repository
    ./scripts/project-locate-doc.sh $repository
done

./scripts/site-prepare-env.sh
./scripts/site-build.sh
