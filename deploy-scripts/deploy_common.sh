#!/usr/bin/env bash
set -e

SLUG="kategory/kategory"
JDK="oraclejdk8"
BRANCH="master"

GRADLE_PROPERTIES_FILE=gradle.properties

function getProperty {
    PROP_KEY=$1
    PROP_VALUE=`cat $GRADLE_PROPERTIES_FILE | grep "$PROP_KEY" | cut -d'=' -f2`
    echo $PROP_VALUE
}

function fail {
    echo "$1"
    exit -1
}

VERSION_NAME=$(getProperty "VERSION_NAME")