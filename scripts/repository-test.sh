#!/bin/bash

cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh

REPOSITORY=$(echo $1 | cut -d- -f1-2)

testProject $REPOSITORY
