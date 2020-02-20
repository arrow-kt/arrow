#!/bin/bash

set -e
export BASEDIR=$(pwd)

echo "Downloading conf..."
git clone git@github.com:arrow-kt/d-arrow-module.git
git clone git@github.com:arrow-kt/d-arrow-site.git

./copy-conf-arrow-ank.sh
./copy-conf-arrow-core.sh
./copy-conf-arrow-site.sh
./copy-conf-arrow-docs.sh
./copy-conf-arrow-integrations.sh
./copy-conf-arrow-fx.sh
./copy-conf-arrow-incubator.sh
./copy-conf-arrow-optics.sh
./copy-conf-arrow-test.sh
./copy-conf-arrow-ui.sh
