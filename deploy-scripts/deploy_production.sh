#!/usr/bin/env bash
deploy_common.sh

if [ "$TRAVIS_REPO_SLUG" != "$SLUG" ]; then
  fail "Failed release deployment: wrong repository. Expected '$SLUG' but was '$TRAVIS_REPO_SLUG'."
elif [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  fail "Failed release deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  fail "Failed release deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
  fail "Failed release deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
else
  echo "Deploying release..."
  ./gradlew bintrayUpload -PdryRun=false
  echo "Release deployed!"
fi