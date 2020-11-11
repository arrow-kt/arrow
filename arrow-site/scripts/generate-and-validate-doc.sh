#!/bin/bash

set -e
. $BASEDIR/arrow-master/scripts/commons4gradle.sh

echo "For version: $VERSION ..."

cd $BASEDIR/arrow-site
git checkout .
git clean -dxf -e vendor -e .bundle
if [ -d $BASEDIR/arrow-site-$VERSION ]; then cp $BASEDIR/arrow-site-$VERSION/docs/_data/sidebar* docs/_data/; fi
perl -pe "s/latest/$VERSION/g" -i docs/_includes/_head-docs.html # TODO

cd $BASEDIR/arrow
git checkout .
git checkout -f $VERSION
perl -pe "s/^VERSION_NAME.*/VERSION_NAME=$VERSION/g" -i gradle.properties
. ./scripts/commons4gradle.sh
replaceOSSbyBintrayRepository "*.gradle"
replaceOSSbyBintrayRepository "gradle/*.gradle"

for repository in $(cat $BASEDIR/arrow/lists/libs.txt); do
    cd $BASEDIR/$repository
    git checkout .
    git checkout -f $(git tag -l --sort=version:refname ${VERSION}* | tail -1)
    replaceGlobalPropertiesbyLocalConf gradle.properties
    perl -pe "s/$(escapeURL $OLD_DIR)/$(escapeURL $NEW_DIR)/g" -i $BASEDIR/arrow/*.gradle # TODO
    if [ -f arrow-docs/build.gradle ]; then
        replaceOSSbyBintrayRepository arrow-docs/build.gradle
    fi
    addArrowDocs $BASEDIR/$repository/settings.gradle
    $BASEDIR/arrow-master/scripts/project-assemble.sh $repository
    $BASEDIR/arrow-master/scripts/project-run-dokka.sh $repository
    $BASEDIR/arrow-master/scripts/project-run-ank.sh $repository
    $BASEDIR/arrow-master/scripts/project-locate-doc.sh $repository
done
