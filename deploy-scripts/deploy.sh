#!/usr/bin/env bash
. $(dirname $0)/deploy_common.sh

echo "Branch '$TRAVIS_BRANCH'"

VERSION_PATTERN_RELEASE=^[0-9]+\.[0-9]+\.[0-9]+$
VERSION_PATTERN_SNAPSHOT=^[0-9]+\.[0-9]+\.[0-9]+-SNAPSHOT$

if [ "$TRAVIS_BRANCH" == "master" ]; then
    if [ "$VERSION_NAME" ~= VERSION_PATTERN_RELEASE ]; then
        . $(dirname $0)/deploy_release.sh
    elif [ "$VERSION_NAME" =~ $VERSION_PATTERN_SNAPSHOT ]; then
        . $(dirname $0)/deploy_snapshot.sh
    fi
else
    echo "Skipped deployment in branch '$TRAVIS_BRANCH'"
fi
