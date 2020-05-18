#!/bin/bash

set -e

ARROW_SITE_DIRECTORY=site
cd $BASEDIR
if [ -d arrow-site ]; then
    ARROW_SITE_DIRECTORY=arrow-site
fi
cp -r $1/arrow-docs/build/site/* $ARROW_SITE_DIRECTORY/build/site/
