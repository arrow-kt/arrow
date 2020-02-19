#!/bin/bash

set -e
echo "Build site ..."
export JEKYLL_ENV=production
cd $BASEDIR/site
bundle install --gemfile $BASEDIR/site/Gemfile --path vendor/bundle
bundle exec jekyll build -b docs/next -s $BASEDIR/site/build/site -d $BASEDIR/site/build/_site
