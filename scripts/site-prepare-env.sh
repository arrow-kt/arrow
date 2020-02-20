#!/bin/bash

set -ex
echo "Prepare environment ..."
sudo apt-get update
sudo apt-get install ruby-dev
sudo gem install bundler --force
sudo gem update --system
gem --version
echo "$( gem list | grep bundler )"
