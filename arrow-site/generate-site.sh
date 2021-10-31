#!/bin/sh

bundle install --gemfile Gemfile --path vendor/bundle
bundle exec jekyll serve -s build/site
