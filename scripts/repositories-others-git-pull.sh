#!/bin/bash

cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh

ARROW_LIB=$(echo $1 | cut -d- -f1-2)

echo "Running git-pull for the rest of the repositories ..."
for repository in $(cat $BASEDIR/arrow/lists/test.txt); do
    if [ "$repository" != "$ARROW_LIB" ]; then
        echo "$repository:"
        cd $BASEDIR/$repository
        git pull
    fi
done

