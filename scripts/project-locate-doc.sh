#!/bin/bash

set -e
cp -r $BASEDIR/$1/arrow-docs/build/site/* $BASEDIR/arrow-site/docs/
