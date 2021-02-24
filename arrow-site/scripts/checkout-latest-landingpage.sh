#!/bin/bash

set -e

export VERSION=$(grep LATEST_VERSION $BASEDIR/arrow-master/gradle.properties | cut -d= -f2)
git clone https://github.com/arrow-kt/arrow-site.git $BASEDIR/arrow-site-$VERSION
LAST_TAG=$(git tag -l --sort=version:refname ${VERSION}* | tail -1)
echo ">> Last tag: $LAST_TAG"
cd $BASEDIR/arrow-site-$VERSION; git checkout $LAST_TAG; cd -

cp $BASEDIR/arrow-site-$VERSION/docs/_code/* docs/landscape/
for code in docs/landscape/*; do
    tail +4 $code > tmp-file; mv tmp-file $code
done
