#!/bin/bash
 
set -ex
cd $BASEDIR/$1
./gradlew clean build
./gradlew check
