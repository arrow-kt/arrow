#!/bin/bash

set -e
echo "Build site ..."
export JEKYLL_ENV=production
cd $BASEDIR/arrow-site
bundle install --gemfile $BASEDIR/arrow-site/Gemfile --path vendor/bundle
bundle exec jekyll build -b docs/next -s $BASEDIR/arrow-site/docs -d $BASEDIR/arrow-site/_site
