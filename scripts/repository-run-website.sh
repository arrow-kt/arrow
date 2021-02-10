#!/bin/bash

set -e
cd $(dirname $0)/../..
export BASEDIR=$(pwd)
. $BASEDIR/arrow/scripts/commons4gradle.sh

ARROW_LIB=$(echo $1 | cut -d- -f1-2)


checkAndDownload arrow-site
addArrowDocs $BASEDIR/${ARROW_LIB}/settings.gradle
$BASEDIR/arrow/scripts/project-build-doc.sh ${ARROW_LIB}
removeArrowDocs $BASEDIR/${ARROW_LIB}/settings.gradle
$BASEDIR/arrow/scripts/project-locate-doc.sh $ARROW_LIB

MAIN_LIBS=(arrow-core arrow-fx arrow-optics)
for library in ${MAIN_LIBS[*]}; do
    if [ "$library" != "$ARROW_LIB" ]; then
        checkAndDownload $library
        addArrowDocs $BASEDIR/$library/settings.gradle
        $BASEDIR/arrow/scripts/project-build-doc.sh $library
        removeArrowDocs $BASEDIR/$library/settings.gradle
        $BASEDIR/arrow/scripts/project-locate-doc.sh $library
    fi
done

$BASEDIR/arrow/scripts/site-run.sh
