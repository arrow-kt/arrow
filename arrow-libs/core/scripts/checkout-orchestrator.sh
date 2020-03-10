#!/bin/bash

ROOTDIR=$1
cd ${ROOTDIR}/..
INSTALLDIR=$(pwd)
cd $INSTALLDIR
if [ ! -d arrow ]; then
    echo "Clone arrow repository in $INSTALLDIR/arrow ..."
    git clone https://github.com/arrow-kt/arrow.git
else
    echo "Updating $INSTALLDIR/arrow ..."
    cd arrow
    git pull
fi
