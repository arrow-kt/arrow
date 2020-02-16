#!/bin/bash

set -e
export BASEDIR=$(pwd)

echo "arrow-ui ..."
cd $BASEDIR/arrow-ui
cp $BASEDIR/d-arrow-module/arrow-ui-repository/README.md .
cp -r $BASEDIR/d-arrow-module/arrow-ui-repository/*gradle* .
sed -i "s/d-arrow/arrow/g" gradle.properties
for module in arrow-*; do
    cp $BASEDIR/d-arrow-module/arrow-ui-repository/$module/build.gradle $module/
    cp $BASEDIR/d-arrow-module/arrow-ui-repository/$module/gradle.properties $module/
done
cp $BASEDIR/d-arrow-module/.gitignore .

mkdir -p .github/workflows/
cp $BASEDIR/d-arrow-module/.github/workflows/*arrow-ui* .github/workflows/
cp $BASEDIR/d-arrow-module/.github/workflows/check* .github/workflows/
sed -i "s/d-arrow-module/arrow-core/g" .github/workflows/*
sed -i "s/d-arrow/arrow/g" .github/workflows/*
sed -i "s/sh arrow-ui-repository/sh/g" .github/workflows/*

git co -b new-conf
git add .
git ci -m "Configuration for the new multi-repo organization"
git push upstream new-conf

#diff -r . ../d-arrow-module/arrow-ui-repository/
