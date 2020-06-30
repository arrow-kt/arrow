#!/bin/bash
 
set -e
cd $BASEDIR/$1

if [ "$1" != "arrow-fx" ]; then
    ./gradlew test
else
    # TODO: Temporary fix until Fx is re-organized
    ./gradlew --no-daemon :arrow-fx:test
    ./gradlew --no-daemon :arrow-fx-test:test
    ./gradlew --no-daemon :arrow-fx-coroutines:test
    ./gradlew --no-daemon :arrow-streams:test
    ./gradlew --no-daemon :arrow-fx-reactor:test
    ./gradlew --no-daemon :arrow-fx-rx2:test
    ./gradlew --no-daemon :arrow-fx-kotlinx-coroutines:test
    ./gradlew --no-daemon :arrow-fx-android:test
    ./gradlew --no-daemon :arrow-benchmarks-fx:test
fi
