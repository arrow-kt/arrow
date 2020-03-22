#!/bin/bash

. $BASEDIR/arrow/scripts/commons4gradle.sh

for lib in $(cat $BASEDIR/arrow/lists/libs.txt); do
    if [ ! -d $BASEDIR/$lib ]; then
        git clone https://github.com/arrow-kt/$lib.git $BASEDIR/$lib
    fi
done

for lib in $(cat $BASEDIR/arrow/lists/libs.txt); do
    echo $lib
    echo $FROM_VERSION
    git log $FROM_VERSION..HEAD
    git log -n1 
    git log $FROM_VERSION..HEAD --pretty='format:- %s [%an]'
done
