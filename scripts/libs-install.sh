#!/bin/bash

cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh

REPOSITORY=$(echo $1 | cut -d- -f1-2)

echo "Check and prepare the environment ..."
for repository in $(cat $BASEDIR/arrow/lists/test.txt); do
    if [ "$repository" != "$REPOSITORY" ]; then
        checkAndDownload $repository
    fi
done

replaceOSSbyLocalRepository $BASEDIR/arrow/generic-conf.gradle
for lib in $(cat $BASEDIR/arrow/lists/libs.txt); do
    installProject $lib
done
#replaceLocalRepositoryByOSS $BASEDIR/arrow/generic-conf.gradle
