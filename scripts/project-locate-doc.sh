#!/bin/bash

set -ex
echo "For $1 ..."
cd $BASEDIR
cp -r $1/**/build/site/* site/build/site/
for module in $1/arrow-*; do
    for submodule in `find $module -mindepth 1 -maxdepth 1 -type d -name "arrow-*"`; do 
        cp -r $submodule/build/site/* site/build/site/
    done
done
