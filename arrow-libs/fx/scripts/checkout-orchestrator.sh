#!/bin/bash

ROOTDIR=$1
cd ${ROOTDIR}/..
BASEDIR=$(pwd)
if [ ! -d arrow ]; then
    echo "Clone arrow repository in $BASEDIR/arrow ..."
    git clone https://github.com/arrow-kt/arrow.git
else
    echo "Updating $INSTALLDIR/arrow ..."
    cd arrow
    git pull
fi
