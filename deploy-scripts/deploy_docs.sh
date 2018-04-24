#!/usr/bin/env bash
. $(dirname $0)/deploy_common.sh

echo "Branch '$TRAVIS_BRANCH'"

if [ "$TRAVIS_BRANCH" == "master" ]; then
    ./gradlew :arrow-docs:gitPublishPush
    echo "Docs deployed!"
else
    echo "Skipped docs deployment in branch '$TRAVIS_BRANCH'"
fi
