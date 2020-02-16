#!/bin/bash

set -e
export BASEDIR=$(pwd)

echo "arrow-docs ..."
cd $BASEDIR/arrow-docs
cp $BASEDIR/d-arrow-module/arrow-docs-repository/README.md .
cp -r $BASEDIR/d-arrow-module/arrow-docs-repository/*gradle* .
sed -i "s/d-arrow/arrow/g" gradle.properties
for module in arrow-*; do
    cp $BASEDIR/d-arrow-module/arrow-docs-repository/$module/build.gradle $module/
    cp $BASEDIR/d-arrow-module/arrow-docs-repository/$module/gradle.properties $module/
done
rm arrow-docs/Gemfile*
rm arrow-docs/.gitignore
rm -rf arrow-docs/docs/*
cp -r $BASEDIR/d-arrow-module/arrow-docs-repository/arrow-docs/docs/static arrow-docs/docs/
cp $BASEDIR/d-arrow-module/.gitignore .

mkdir -p .github/workflows/
cp $BASEDIR/d-arrow-module/.github/workflows/*arrow-docs* .github/workflows/
cp $BASEDIR/d-arrow-module/.github/workflows/check* .github/workflows/
sed -i "s/d-arrow-module/arrow-core/g" .github/workflows/*
sed -i "s/d-arrow/arrow/g" .github/workflows/*
sed -i "s/sh arrow-docs-repository/sh/g" .github/workflows/*

git co -b new-conf
git add .
git ci -m "Configuration for the new multi-repo organization"
git push upstream new-conf

#diff -r . ../d-arrow-module/arrow-docs-repository/
