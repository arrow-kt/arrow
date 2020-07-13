#!/bin/bash

INCLUDE_ARROW_DOCS="include 'arrow-docs'"
OSS_REPOSITORY="https://oss.jfrog.org/artifactory/oss-snapshot-local/"
BINTRAY_REPOSITORY="https://dl.bintray.com/arrow-kt/arrow-kt/"
MAVEN_LOCAL_REPOSITORY="mavenLocal()"
ERROR_LOG=error.log
OLD_DIR="https://raw.githubusercontent.com/arrow-kt/arrow/master"
NEW_DIR="file://$BASEDIR/arrow"



function escapeURL()
{
    URL=$1
    echo $URL | perl -pe "s/\//\\\\\//g"
}

function replaceOSSbyLocalRepository()
{
    echo "Replacing OSS by local repository ($1)..."
    perl -pe "s/maven \{ url \"$(escapeURL $OSS_REPOSITORY)\" }/$MAVEN_LOCAL_REPOSITORY/g" -i $1
}

function replaceLocalRepositorybyOSS()
{
    echo "Replacing local repository by OSS ($1) ..."
    perl -pe "s/$MAVEN_LOCAL_REPOSITORY/maven \{ url \"$(escapeURL $OSS_REPOSITORY)\" }/g" -i $1
}

function replaceOSSbyBintrayRepository()
{
    echo "Replacing OSS by Bintray repository ($1) ..."
    perl -pe "s/maven \{ url \"$(escapeURL $OSS_REPOSITORY)\" }/maven \{ url \"$(escapeURL $BINTRAY_REPOSITORY)\" }/g" -i $1
}

function addLocalRepositoryBeforeOSS()
{
    echo "Adding local repository before OSS ($1) ..."
    perl -pe "s/maven \{ url \"$(escapeURL $OSS_REPOSITORY)\" }/$MAVEN_LOCAL_REPOSITORY\nmaven \{ url \"$(escapeURL $OSS_REPOSITORY)\" }/g" -i $1
}

function removeArrowDocs()
{
    echo "Removing Arrow Docs ($1)..."
    perl -pe "s/$INCLUDE_ARROW_DOCS//g" -i $1
}

function addArrowDocs()
{
    echo "Adding Arrow Docs ($1)..."
    echo $INCLUDE_ARROW_DOCS >> $1
}

function checkAndDownloadViaSSH()
{
    REPOSITORY=$1

    if [ ! -d $BASEDIR/$REPOSITORY ]; then
        echo "Creating $BASEDIR/$REPOSITORY ..."
        git clone git@github.com:arrow-kt/${REPOSITORY}.git $BASEDIR/$REPOSITORY
    fi
}

function lookForBranchInPullRequests()
{
    BRANCH=$1

    hub pr list --limit 100 -s open --format='%H%n' | grep $BRANCH || true
}

function checkoutBranchIfFound()
{
    REPOSITORY=$1
    BRANCH=$2

    cd $BASEDIR/$REPOSITORY
    echo "Looking for $BRANCH in $REPOSITORY ..."
    FOUND_BRANCH=$(lookForBranchInPullRequests $BRANCH)
    if [ "$FOUND_BRANCH" == $BRANCH ] || [[ "$FOUND_BRANCH" =~ ":$BRANCH"$ ]]; then
        echo "$FOUND_BRANCH found for $REPOSITORY!"
        if [[ $FOUND_BRANCH =~ .+:.+ ]]; then
            OWNER=$(echo $FOUND_BRANCH | cut -d: -f1)
            git pull --rebase https://github.com/$OWNER/$REPOSITORY.git $BRANCH
        else
            git checkout $FOUND_BRANCH
        fi
    fi
}

function checkAndDownloadViaHTTPS()
{
    REPOSITORY=$1
    BRANCH=$2

    if [ ! -d $BASEDIR/$REPOSITORY ]; then
        echo "Creating $BASEDIR/$REPOSITORY ..."
        git clone https://github.com/arrow-kt/${REPOSITORY}.git $BASEDIR/$REPOSITORY --depth 1 --no-single-branch
        if [ $BRANCH != "master" ]; then
            checkoutBranchIfFound $REPOSITORY $BRANCH
        fi
    fi
}

function updateOrchestrator()
{
    BRANCH=$1

    sleep 180
    cd $BASEDIR/arrow
    if [ "$BRANCH" == "master" ]; then
        echo "Updating master branch for arrow repository ..."
        git pull --rebase origin master
    else
        checkoutBranchIfFound arrow $BRANCH
    fi
}

function replaceGlobalPropertiesbyLocalConf()
{
    echo "Replacing global properties by local conf ($1) ..."
    perl -pe "s/^COMMON_SETUP.*/COMMON_SETUP=$(escapeURL $NEW_DIR)\/setup.gradle/g" -i $1
    perl -pe "s/^GENERIC_CONF.*/GENERIC_CONF=$(escapeURL $NEW_DIR)\/generic-conf.gradle/g" -i $1
    perl -pe "s/^SUBPROJECT_CONF.*/SUBPROJECT_CONF=$(escapeURL $NEW_DIR)\/subproject-conf.gradle/g" -i $1
    perl -pe "s/^DOC_CONF.*/DOC_CONF=$(escapeURL $NEW_DIR)\/doc-conf.gradle/g" -i $1
    perl -pe "s/^PUBLISH_CONF.*/PUBLISH_CONF=$(escapeURL $NEW_DIR)\/publish-conf.gradle/g" -i $1
    perl -pe "s/^ANDROID_CONF.*/ANDROID_CONF=$(escapeURL $NEW_DIR)\/android-conf.gradle/g" -i $1
    perl -pe "s/$(escapeURL $OLD_DIR)/$(escapeURL $NEW_DIR)/g" -i $BASEDIR/arrow/setup.gradle
}

function replaceLocalConfbyGlobalProperties()
{
    echo "Replacing local conf by global properties ($1) ..."
    perl -pe "s/^COMMON_SETUP.*/COMMON_SETUP=$(escapeURL $OLD_DIR)\/setup.gradle/g" -i $1
    perl -pe "s/^GENERIC_CONF.*/GENERIC_CONF=$(escapeURL $OLD_DIR)\/generic-conf.gradle/g" -i $1
    perl -pe "s/^SUBPROJECT_CONF.*/SUBPROJECT_CONF=$(escapeURL $OLD_DIR)\/subproject-conf.gradle/g" -i $1
    perl -pe "s/^DOC_CONF.*/DOC_CONF=$(escapeURL $OLD_DIR)\/doc-conf.gradle/g" -i $1
    perl -pe "s/^PUBLISH_CONF.*/PUBLISH_CONF=$(escapeURL $OLD_DIR)\/publish-conf.gradle/g" -i $1
    perl -pe "s/^ANDROID_CONF.*/ANDROID_CONF=$(escapeURL $OLD_DIR)\/android-conf.gradle/g" -i $1
    perl -pe "s/$(escapeURL $NEW_DIR)/$(escapeURL $OLD_DIR)/g" -i $BASEDIR/arrow/setup.gradle
}

function useLocalSetup()
{
    perl -pe "s/$(escapeURL $OLD_DIR)/$(escapeURL $NEW_DIR)/g" -i $BASEDIR/arrow/setup.gradle
}

function manageExitCode()
{
    EXIT_CODE=$1

    if [[ $EXIT_CODE -ne 0 ]]; then
        cat $ERROR_LOG
        rm $ERROR_LOG
        exit $EXIT_CODE
    fi
}

function runAndManageExitCode()
{
    PROJECT=$1
    COMMAND=$2

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$PROJECT/gradle.properties
    $COMMAND $PROJECT 2> $ERROR_LOG
    EXIT_CODE=$?
    #replaceLocalConfbyGlobalProperties $BASEDIR/$PROJECT/gradle.properties
    manageExitCode $EXIT_CODE
}

function installWithLocalConf()
{
    PROJECT=$1
    runAndManageExitCode "$PROJECT" "$BASEDIR/arrow/scripts/project-install.sh"
}

function checkWithLocalConf()
{
    PROJECT=$1
    runAndManageExitCode "$PROJECT" "$BASEDIR/arrow/scripts/project-check.sh"
}

function buildDoc()
{
    PROJECT=$1

    addArrowDocs $BASEDIR/$PROJECT/settings.gradle
    $BASEDIR/arrow/scripts/project-build-doc.sh $PROJECT 2> $ERROR_LOG
    EXIT_CODE=$?
    removeArrowDocs $BASEDIR/$PROJECT/settings.gradle
    manageExitCode $EXIT_CODE
}

function buildDocWithLocalConf()
{
    PROJECT=$1

    replaceGlobalPropertiesbyLocalConf $BASEDIR/$PROJECT/gradle.properties
    buildDoc $PROJECT
}
