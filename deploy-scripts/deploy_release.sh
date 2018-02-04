#!/usr/bin/env bash
. $(dirname $0)/deploy_common.sh

VERSION_PATTERN=^[0-9]+\.[0-9]+\.[0-9]+$

echo "Deploying release '$VERSION_NAME' ..."

if [ "$TRAVIS_REPO_SLUG" != "$SLUG" ]; then
  fail "Failed release deployment: wrong repository. Expected '$SLUG' but was '$TRAVIS_REPO_SLUG'."
elif [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  fail "Failed release deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  fail "Failed release deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
  fail "Failed release deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
elif ! [[ "$VERSION_NAME" =~ $VERSION_PATTERN ]]; then
  fail "Failed release deployment: wrong version. Expected '$VERSION_NAME' to have pattern 'X.Y.Z'"
else
  ./gradlew uploadArchives
  echo "Release '$VERSION_NAME' deployed!"
fi