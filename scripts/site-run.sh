#!/bin/bash

set -e
echo "Build site ..."
cd $BASEDIR/arrow-site
bundle install --gemfile $BASEDIR/arrow-site/Gemfile --path vendor/bundle
bundle exec jekyll serve -s $BASEDIR/arrow-site/build/site
