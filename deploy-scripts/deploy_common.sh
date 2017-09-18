#!/usr/bin/env bash
. deploy_functions.sh
set -e

SLUG="kategory/kategory"
JDK="oraclejdk8"
BRANCH="master"
VERSION_NAME=$(getProperty "VERSION_NAME")