#!/bin/bash

set -e
export BASEDIR=$(pwd)

echo "arrow-ank ..."
cd $BASEDIR/arrow-ank
cp $BASEDIR/d-arrow-module/arrow-ank-repository/README.md .
cp -r $BASEDIR/d-arrow-module/arrow-ank-repository/*gradle* .
sed -i "s/d-arrow/arrow/g" gradle.properties
for module in arrow-*; do
    cp $BASEDIR/d-arrow-module/arrow-ank-repository/$module/build.gradle $module/
    cp $BASEDIR/d-arrow-module/arrow-ank-repository/$module/gradle.properties $module/
done
cp $BASEDIR/d-arrow-module/.gitignore .
cp $BASEDIR/d-arrow-module/LICENSE.md .
cp $BASEDIR/d-arrow-module/CONTRIBUTING.md .

mkdir -p .github/workflows/
cp -r $BASEDIR/d-arrow-module/.github/ISSUE_TEMPLATE .github/
cp $BASEDIR/d-arrow-module/.github/workflows/*arrow-ank* .github/workflows/
cp $BASEDIR/d-arrow-module/.github/workflows/check* .github/workflows/
sed -i "s/d-arrow-module/arrow-core/g" .github/workflows/*
sed -i "s/d-arrow/arrow/g" .github/workflows/*
sed -i "s/sh arrow-ank-repository/sh/g" .github/workflows/*
sed -i "s/on: pull_request/on:/g" .github/workflows/sync-release-branch-arrow-*.yml
sed -i "s/#  schedule/  schedule/g" .github/workflows/sync-release-branch-arrow-*.yml
sed -i "s/#    - cron/    - cron/g" .github/workflows/sync-release-branch-arrow-*.yml

git co -b new-conf
git add .
git ci -m "Configuration for the new multi-repo organization"
git push upstream new-conf

#diff -r . ../d-arrow-module/arrow-ank-repository/
