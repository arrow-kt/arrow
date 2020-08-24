#!/bin/bash

set -e
if [ ! -d $BASEDIR/arrow-site ]; then
    git clone https://github.com/arrow-kt/arrow-site.git $BASEDIR/arrow-site
fi
