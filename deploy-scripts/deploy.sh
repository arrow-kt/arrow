#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" == "master" ]; then
    if [ -n "$TRAVIS_TAG" ]; then
        ./deploy_release.sh;
    else
        ./deploy_snapshot.sh;
    fi
else
    echo "Skipped deployment in branch '$TRAVIS_BRANCH'"
fi