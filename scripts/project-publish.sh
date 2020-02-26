#!/bin/bash

set -e        
cd $BASEDIR/$1
./gradlew uploadArchives
echo "$(cat $BASEDIR/arrow/gradle.properties | grep VERSION_NAME | cut -d'=' -f2) deployed!"
