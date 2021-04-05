#!/bin/bash
 
set -ex

echo "$JAVA_HOME_8_X64/bin" >> $GITHUB_PATH
echo "JAVA_HOME=$JAVA_HOME_8_X64" >> $GITHUB_ENV
LATEST_PUBLISHED_VERSION=$(curl -L https://repo1.maven.org/maven2/io/arrow-kt/arrow-core/maven-metadata.xml | ggrep -oP '<latest>\K[^<]*')
if [ "$LATEST_PUBLISHED_VERSION" == "" ]; then exit 1; fi
RELEASE_VERSION=$(grep LATEST_VERSION $BASEDIR/gradle.properties | cut -d= -f2)
echo "LATEST_PUBLISHED_VERSION=$LATEST_PUBLISHED_VERSION" >> $GITHUB_ENV
echo "RELEASE_VERSION=$RELEASE_VERSION" >> $GITHUB_ENV
echo "NEW_RELEASE_VERSION_EXISTS="$([ "$LATEST_PUBLISHED_VERSION" == "$RELEASE_VERSION" ] && echo '0' || echo '1') >> $GITHUB_ENV
