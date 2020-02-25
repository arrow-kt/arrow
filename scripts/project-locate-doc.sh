#!/bin/bash

set -ex

cd $BASEDIR
cp -r $1/arrow-docs/build/site/* site/build/site/
