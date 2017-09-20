#!/usr/bin/env bash

function getProperty {
    PROP_KEY=$1
    PROP_VALUE=`cat gradle.properties | grep "$PROP_KEY" | cut -d'=' -f2`
    echo $PROP_VALUE
}

function fail {
    echo "$1"
    exit -1
}