#!/bin/bash
 
set -e
cd $BASEDIR/$1

if [ "$1" != "arrow-fx" ]; then
    ./gradlew test
else
    # TODO: Temporary fix until Fx is re-organized
    ./gradlew :arrow-fx:test
    ./gradlew :arrow-fx-test:test
    ./gradlew :arrow-fx-coroutines:test
    ./gradlew :arrow-streams:test
    ./gradlew :arrow-fx-reactor:test
    ./gradlew :arrow-fx-rx2:test
    ./gradlew :arrow-fx-kotlinx-coroutines:test
    ./gradlew :arrow-fx-android:test
    ./gradlew :arrow-benchmarks-fx:test
fi
