#!/bin/bash

cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh

echo "Running git-pull for all the repositories ..."
for repository in $(cat $BASEDIR/arrow/lists/test.txt); do
    echo "$repository:"
    cd $BASEDIR/$repository
    git pull
done

