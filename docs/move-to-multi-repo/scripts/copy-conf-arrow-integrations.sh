#!/bin/bash

set -e
export BASEDIR=$(pwd)

echo "arrow-integrations ..."
cd $BASEDIR/arrow-integrations
cp $BASEDIR/d-arrow-module/arrow-integrations-repository/README.md .
cp $BASEDIR/d-arrow-module/arrow-integrations-repository/.editorconfig .
cp -r $BASEDIR/d-arrow-module/arrow-integrations-repository/*gradle* .
sed -i "s/d-arrow/arrow/g" gradle.properties
for module in arrow-*; do
    cp $BASEDIR/d-arrow-module/arrow-integrations-repository/$module/build.gradle $module/
done
cp $BASEDIR/d-arrow-module/.gitignore .
cp $BASEDIR/d-arrow-module/LICENSE.md .
cp $BASEDIR/d-arrow-module/CONTRIBUTING.md .

mkdir -p .github/workflows/
cp -r $BASEDIR/d-arrow-module/.github/ISSUE_TEMPLATE .github/
cp $BASEDIR/d-arrow-module/.github/workflows/*arrow-integrations* .github/workflows/
sed -i "s/d-arrow-module/arrow-integrations/g" .github/workflows/*
sed -i "s/d-arrow/arrow/g" .github/workflows/*
sed -i "s/sh arrow-integrations-repository/sh/g" .github/workflows/*
sed -i "s/on: pull_request/on:/g" .github/workflows/sync-release-branch-arrow-*.yml
sed -i "s/#  schedule/  schedule/g" .github/workflows/sync-release-branch-arrow-*.yml
sed -i "s/#    - cron/    - cron/g" .github/workflows/sync-release-branch-arrow-*.yml

git co -b new-conf
git add .
git ci -m "Configuration for the new multi-repo organization"
git push upstream new-conf

git co master
git co -b global-checks
mkdir -p .github/workflows/
cp $BASEDIR/d-arrow-module/.github/workflows/check* .github/workflows/
sed -i "s/d-arrow-module/arrow-integrations/g" .github/workflows/*
sed -i "s/d-arrow/arrow/g" .github/workflows/*
sed -i "s/sh arrow-integrations-repository/sh/g" .github/workflows/*
git add .
git ci -m "Configuration: add global checks"
git push upstream global-checks

#diff -r . ../d-arrow-module/arrow-integrations-repository/
