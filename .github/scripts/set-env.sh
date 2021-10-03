#!/bin/bash
 
set -ex

# echo "$JAVA_HOME_8_X64/bin" >> $GITHUB_PATH
# echo "JAVA_HOME=$JAVA_HOME_8_X64" >> $GITHUB_ENV

NEW_RELEASE_VERSION_EXISTS=0
if [ "$GITHUB_REF" == "refs/heads/main" ]; then
    if [[ $OSTYPE == 'darwin'* ]]; then
      LATEST_PUBLISHED_VERSION=$(curl -L https://repo1.maven.org/maven2/io/arrow-kt/arrow-core/maven-metadata.xml | ggrep -oP '<latest>\K[^<]*')
    else
      export LC_ALL=en_US.utf8;
      LATEST_PUBLISHED_VERSION=$(curl -L https://repo1.maven.org/maven2/io/arrow-kt/arrow-core/maven-metadata.xml | grep -oP '<latest>\K[^<]*')
    fi

    if [ "$LATEST_PUBLISHED_VERSION" == "" ]; then exit 1; fi
    RELEASE_VERSION=$(grep "projects.latestVersion" $BASEDIR/gradle.properties | cut -d= -f2)
    if [ "$LATEST_PUBLISHED_VERSION" != "$RELEASE_VERSION" ]; then NEW_RELEASE_VERSION_EXISTS=1; fi
else
    echo "Into release branch ..."
    BRANCH_VERSION=$(echo $GITHUB_REF | cut -d/ -f4)
    RELEASE_VERSION=$(grep "projects.latestVersion" $BASEDIR/gradle.properties | cut -d= -f2)
    if [ "$BRANCH_VERSION" == "$RELEASE_VERSION" ]; then
        NEW_RELEASE_VERSION_EXISTS=1
    else
        perl -pe "s/^projects.version=.*/projects.version=$BRANCH_VERSION-SNAPSHOT/g" -i $BASEDIR/gradle.properties
    fi
fi

if [ $NEW_RELEASE_VERSION_EXISTS == 1 ]; then
    perl -pe "s/^projects.version=.*/projects.version=$RELEASE_VERSION/g" -i $BASEDIR/gradle.properties
    perl -pe "s/^org.gradle.parallel=.*/org.gradle.parallel=false/g" -i $BASEDIR/gradle.properties
fi

echo "LATEST_PUBLISHED_VERSION=$LATEST_PUBLISHED_VERSION" >> $GITHUB_ENV
echo "RELEASE_VERSION=$RELEASE_VERSION" >> $GITHUB_ENV
echo "NEW_RELEASE_VERSION_EXISTS=$NEW_RELEASE_VERSION_EXISTS" >> $GITHUB_ENV
