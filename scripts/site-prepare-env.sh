#!/bin/bash

set -e
echo "Prepare environment ..."
sudo rm -rf /etc/apt/sources.list.d/microsoft*
sudo rm -rf /etc/apt/sources.list.d/azure*
sudo rm -rf /etc/apt/sources.list.d/dotnet*
sudo apt-get update
sudo apt-get install ruby-dev
sudo gem install bundler --force
sudo gem update --system
gem --version
echo "$( gem list | grep bundler )"
