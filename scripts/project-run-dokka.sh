#!/bin/bash

set -e

echo "For $1 ..."
cd $BASEDIR/$1
./gradlew dokka
