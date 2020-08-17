#!/bin/bash

set -e
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh

ARROW_LIB=$(echo $1 | cut -d- -f1-2)


checkAndDownloadViaSSH arrow-site
perl -pe "s/\/docs//g" -i $BASEDIR/arrow-site/docs/_data/features.yml
$BASEDIR/arrow/scripts/site-run-ank.sh

cd $BASEDIR/${ARROW_LIB}; ./gradlew buildArrowDoc
$BASEDIR/arrow/scripts/project-locate-doc.sh $ARROW_LIB

MAIN_LIBS=(arrow-core arrow-fx arrow-optics arrow-incubator)
for library in ${MAIN_LIBS[*]}; do
    if [ "$library" != "$ARROW_LIB" ]; then
        checkAndDownloadViaSSH $library
        cd $BASEDIR/$library; ./gradlew buildArrowDoc
        $BASEDIR/arrow/scripts/project-locate-doc.sh $library
    fi
done

$BASEDIR/arrow/scripts/site-run.sh
