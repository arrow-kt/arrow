#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" == "master" ]; then
    if [ -n "$TRAVIS_TAG" ]; then
        . $(dirname $0)/deploy_release.sh
    else
        . $(dirname $0)/deploy_snapshot.sh
    fi
else
    echo "Skipped deployment in branch '$TRAVIS_BRANCH'"
fi