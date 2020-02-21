#!/bin/bash

function removeAnkFromCustomBuild()
{
    echo "Removing ANK from custom build ($1) ..."
    if [[ "$1" =~ ^.*arrow-ank-gradle.*$ ]]; then return; fi
    sed -i "s/classpath \"io.arrow-kt/\/\/classpath \"io.arrow-kt/g" $1
    while read -r line; do if [ "$line" != "ank {" ]; then echo "$line" >> build.gradle.tmp; else break; fi; done < $1
    mv build.gradle.tmp $1
}

function replaceOSSbyLocalRepository()
{
    echo "Replacing OSS by local repository ($1) ..."
    sed -i "s/repositories {/repositories { \\nmavenLocal()/g" $1
    sed -i "s/maven { url \"https:\/\/oss.jfrog.org\/artifactory\/oss-snapshot-local\/\" }//g" $1
}

function removeAnkFromCommonBuild()
{
    echo "Removing ANK from common build ($1) ..."
    while read -r line; do if [[ ! "$line" =~ ^.*ank-gradle-plugin.*$ ]]; then echo "$line" >> doc-conf.gradle.tmp; else break; fi; done < $1
    mv doc-conf.gradle.tmp $1
}

function replaceGlobalPropertiesbyLocalConf()
{
    echo "Replacing global properties by local conf ($1) ..."
    sed -i "s/GENERIC_CONF/#GENERIC_CONF/g" $1
    sed -i "s/PUBLISH_CONF/#PUBLISH_CONF/g" $1
    sed -i "s/DOC_CONF/#DOC_CONF/g" $1
    echo "GENERIC_CONF=file://$BASEDIR/generic-conf.gradle" >> $1
    echo "PUBLISH_CONF=file://$BASEDIR/publish-conf.gradle" >> $1
    echo "DOC_CONF=file://$BASEDIR/doc-conf.gradle" >> $1
}

function addLocalRepository()
{
    sed -i "s/repositories {/repositories { \\nmavenLocal()/g" $1
}

function useLocalGenericConf()
{
    sed -i "s/GENERIC_CONF/#GENERIC_CONF/g" $1
    echo "GENERIC_CONF=file://$BASEDIR/arrow/generic-conf.gradle" >> $1   
}
