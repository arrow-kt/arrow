#!/bin/bash

ROOTDIR=$1
cd ${ROOTDIR}/..
BASEDIR=$(pwd)
if [ ! -d arrow ]; then
    echo "Clone arrow repository in $BASEDIR/arrow ..."
    git clone git@github.com:arrow-kt/arrow.git 2> /dev/null || git clone https://github.com/arrow-kt/arrow.git
fi
