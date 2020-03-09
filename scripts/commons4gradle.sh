#!/bin/bash

INCLUDE_ARROW_DOCS="include 'arrow-docs'"
OSS_REPOSITORY="maven { url \"https:\/\/oss.jfrog.org\/artifactory\/oss-snapshot-local\/\" }"
MAVEN_LOCAL_REPOSITORY="mavenLocal()"
ERROR_LOG=error.log

function escapeURL()
{
    URL=$1
    echo $URL | sed "s/\//\\\\\//g"
}

function replaceOSSbyLocalRepository()
{
    echo "Replacing OSS by local repository ($1)..."
    sed -i "s/$OSS_REPOSITORY/$MAVEN_LOCAL_REPOSITORY/g" $1
}

function replaceLocalRepositorybyOSS()
{
    echo "Replacing local repository by OSS ($1) ..."
    sed -i "s/$MAVEN_LOCAL_REPOSITORY/$OSS_REPOSITORY/g" $1
}

function removeArrowDocs()
{
    echo "Removing Arrow Docs ($1)..."
    sed "/$INCLUDE_ARROW_DOCS/d" $1 > $1.tmp ; mv $1.tmp $1
}

function addArrowDocs()
{
    echo "Adding Arrow Docs ($1)..."
    echo $INCLUDE_ARROW_DOCS >> $1
}

function checkAndDownload()
{
    REPOSITORY=$1

    if [ ! -d $BASEDIR/$REPOSITORY ]; then
        cd $BASEDIR
        echo "Creating $BASEDIR/$REPOSITORY ..."
        git clone git@github.com:arrow-kt/${REPOSITORY}.git
    fi
}

function replaceGlobalPropertiesbyLocalConf()
{
    NEW_URL="file://$BASEDIR/arrow/generic-conf.gradle"

    echo "Replacing global properties by local conf ($1) ..."
    sed -i "s/^GENERIC_CONF.*/GENERIC_CONF=$(escapeURL $NEW_URL)/g" $1
}

function replaceLocalConfbyGlobalProperties()
{
    OLD_URL="https://raw.githubusercontent.com/arrow-kt/arrow/master/generic-conf.gradle"

    echo "Replacing local conf by global properties ($1) ..."
    sed -i "s/^GENERIC_CONF.*/GENERIC_CONF=$(escapeURL $OLD_URL)/g" $1
}

function manageExitCode()
{
    EXIT_CODE=$1
    PROJECT=$2

    if [[ $EXIT_CODE -ne 0 ]]; then
        cat $ERROR_LOG
        rm $ERROR_LOG
        undoLocalConfChange $PROJECT
        #undoGlobalConfChange
        exit $EXIT_CODE
    fi
}

function runAndManageExitCode()
{
    PROJECT=$1
    COMMAND=$2

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$PROJECT/gradle.properties
    $COMMAND $PROJECT 2> $ERROR_LOG
    manageExitCode $? $PROJECT
    #replaceLocalConfbyGlobalProperties $BASEDIR/$PROJECT/gradle.properties
}

function installProject()
{
    PROJECT=$1
    runAndManageExitCode "$PROJECT" "$BASEDIR/arrow/scripts/project-install.sh"
}

function testProject()
{
    PROJECT=$1
    runAndManageExitCode "$PROJECT" "$BASEDIR/arrow/scripts/project-test.sh"
}

function buildDoc()
{
    PROJECT=$1

    addArrowDocs $BASEDIR/$PROJECT/settings.gradle
    $BASEDIR/arrow/scripts/project-build-doc.sh $PROJECT 2> $ERROR_LOG
    manageExitCode $? $PROJECT
    removeArrowDocs $BASEDIR/$PROJECT/settings.gradle
}
