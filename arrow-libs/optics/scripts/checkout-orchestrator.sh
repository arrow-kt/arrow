#!/bin/bash

ROOTDIR=$1
cd ${ROOTDIR}/..
BASEDIR=$(pwd)
if [ ! -d arrow ]; then
    echo "Clone arrow repository in $BASEDIR/arrow ..."
    git clone git@github.com:arrow-kt/arrow.git
fi
