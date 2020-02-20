#!/bin/bash

set -e
export BASEDIR=$(pwd)

echo "arrow-site ..."
cd $BASEDIR/arrow-site
cp $BASEDIR/d-arrow-site/README.md .
cp $BASEDIR/d-arrow-site/LICENSE.md .
cp $BASEDIR/d-arrow-site/CONTRIBUTING.md .
cp -r $BASEDIR/d-arrow-site/*gradle* .
sed -i "s/d-arrow/arrow/g" gradle.properties
cp $BASEDIR/d-arrow-site/update-versions.txt .
cp $BASEDIR/d-arrow-site/Gemfile .
cp $BASEDIR/d-arrow-site/.gitignore .
cp -r $BASEDIR/d-arrow-site/.github .
rm -rf docs/docs
git co -b new-conf
git add .
git ci -m "Configuration for the new multi-repo organization"
git push upstream new-conf

#diff -x .git -r . ../d-arrow-site/
