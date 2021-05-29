#!/bin/bash
 
set -ex

echo "$JAVA_HOME_8_X64/bin" >> $GITHUB_PATH
echo "JAVA_HOME=$JAVA_HOME_8_X64" >> $GITHUB_ENV

if [ "$GITHUB_REF" == "refs/heads/main" ]; then
    LATEST_PUBLISHED_VERSION=$(curl -L https://repo1.maven.org/maven2/io/arrow-kt/arrow-core/maven-metadata.xml | ggrep -oP '<latest>\K[^<]*')
    if [ "$LATEST_PUBLISHED_VERSION" == "" ]; then exit 1; fi
    RELEASE_VERSION=$(grep LATEST_VERSION $BASEDIR/gradle.properties | cut -d= -f2)
    NEW_RELEASE_VERSION_EXISTS=$([ "$LATEST_PUBLISHED_VERSION" == "$RELEASE_VERSION" ] && echo '0' || echo '1')
else
    echo "Into release branch ..."
    BRANCH_VERSION=$(echo $GITHUB_REF | cut -d/ -f4)
    RELEASE_VERSION=$(grep LATEST_VERSION $BASEDIR/gradle.properties | cut -d= -f2)
    NEW_RELEASE_VERSION_EXISTS=$([ "$BRANCH_VERSION" == "$RELEASE_VERSION" ] && echo '1' || echo '0')
    if [ $NEW_RELEASE_VERSION_EXISTS == '0' ]; then
        perl -pe "s/^VERSION_NAME=.*/VERSION_NAME=$BRANCH_VERSION-SNAPSHOT/g" -i $BASEDIR/gradle.properties
    fi
fi

if [ $NEW_RELEASE_VERSION_EXISTS == '1' ]; then
    perl -pe "s/^VERSION_NAME=.*/VERSION_NAME=$RELEASE_VERSION/g" -i $BASEDIR/gradle.properties
    perl -pe "s/^org.gradle.parallel=.*/org.gradle.parallel=false/g" -i $BASEDIR/gradle.properties
fi

echo "LATEST_PUBLISHED_VERSION=$LATEST_PUBLISHED_VERSION" >> $GITHUB_ENV
echo "RELEASE_VERSION=$RELEASE_VERSION" >> $GITHUB_ENV
echo "NEW_RELEASE_VERSION_EXISTS=$NEW_RELEASE_VERSION_EXISTS" >> $GITHUB_ENV
