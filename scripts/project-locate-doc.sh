#!/bin/bash

set -ex

INITIAL_PATH=""
if [ $# -eq 1 ]; then
    echo "For $1 ..."
    INITIAL_PATH="$1/"
fi

cd $BASEDIR
cp -r ${INITIAL_PATH}**/build/site/* site/build/site/
for module in ${INITIAL_PATH}arrow-*; do
    for submodule in `find $module -mindepth 1 -maxdepth 1 -type d -name "arrow-*"`; do 
        cp -r $submodule/build/site/* site/build/site/
    done
done
