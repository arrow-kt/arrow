#!/bin/bash

set -ex        
cd $BASEDIR/$1
./gradlew publishToMavenLocal
