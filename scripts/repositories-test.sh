#!/bin/bash

cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh

for repository in $(cat $BASEDIR/arrow/lists/test.txt); do
    testProject $repository
done
