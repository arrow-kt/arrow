#!/bin/bash

set -e        
cd $BASEDIR/$1
./gradlew publishToMavenLocal
