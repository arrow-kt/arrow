#!/bin/bash

set -ex
echo "For $1 ..."
cd $BASEDIR
cp -r $1/build/site/* site/build/site/
