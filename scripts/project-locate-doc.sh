#!/bin/bash

set -e

cd $BASEDIR
cp -r $1/arrow-docs/build/site/* site/build/site/
