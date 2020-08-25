#!/bin/bash

set -e        
cd $BASEDIR/$1
./gradlew publish
echo "$(cat $BASEDIR/arrow/gradle.properties | grep VERSION_NAME | cut -d'=' -f2) deployed!"
