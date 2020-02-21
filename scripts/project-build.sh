#!/bin/bash

set -e
export JAVA_OPTS="-Xms512m -Xmx1024m"
. $BASEDIR/arrow/scripts/commons.sh

if [ "$1" != "arrow-test" ]; then

    $BASEDIR/arrow/scripts/project-install.sh $1
    
    cd $BASEDIR/arrow
    addLocalRepository generic-conf.gradle

    cd $BASEDIR
    git clone https://github.com/arrow-kt/arrow-test.git
    cd arrow-test
    useLocalGenericConf gradle.properties
    $BASEDIR/arrow/scripts/project-install.sh arrow-test

    cd $BASEDIR/$1
    useLocalGenericConf gradle.properties
    ./gradlew test
    ./gradlew check

else
    # TODO
    $BASEDIR/arrow/scripts/project-simple-build.sh $1
fi
