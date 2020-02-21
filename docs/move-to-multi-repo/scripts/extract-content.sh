#!/bin/bash

set -e
export BASEDIR=$(pwd)

echo "ANK ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-ank
cd arrow-ank

git-filter-repo --path modules/ank/arrow-ank \
 --path modules/ank/arrow-ank-gradle \
 --path-rename modules/ank/arrow-ank:arrow-ank \
 --path-rename modules/ank/arrow-ank-gradle:arrow-ank-gradle \
 --path ank-core

git remote add upstream git@github.com:arrow-kt/arrow-ank.git
git push --all upstream

echo "CORE ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-core
cd arrow-core

git-filter-repo --path modules/core/arrow-core \
 --path modules/core/arrow-core-data \
 --path modules/meta/arrow-meta \
 --path modules/meta/arrow-annotations \
 --path modules/core/arrow-syntax \
 --path-rename modules/core/arrow-core:arrow-core \
 --path-rename modules/core/arrow-core-data:arrow-core-data \
 --path-rename modules/meta/arrow-meta:arrow-meta \
 --path-rename modules/meta/arrow-annotations:arrow-annotations \
 --path-rename modules/core/arrow-syntax:arrow-syntax \
 --path modules/dagger \
 --path modules/meta/arrow-meta \
 --path arrow-annotations-processor-test \
 --path arrow-annotations-processor \
 --path arrow-annotations \
 --path arrow-core \
 --path modules/core/arrow-data \
 --path arrow-dagger \
 --path arrow-data \
 --path arrow-syntax \
 --path arrow-typeclasses \
 --path core \
 --path funktionale-collections \
 --path funktionale-complement \
 --path funktionale-currying \
 --path funktionale-either \
 --path funktionale-experimental \
 --path funktionale-memoization \
 --path funktionale-partials \
 --path funktionale-reverse \
 --path funktionale-utils \
 --path kategory-annotations-processor \
 --path kategory-annotations \
 --path kategory-core \
 --path modules/core/arrow-annotations-processor \
 --path modules/core/arrow-core-extensions \
 --path modules/core/arrow-core \
 --path modules/core/arrow-extras \
 --path modules/core/arrow-free \
 --path modules/core/arrow-mtl \
 --path modules/core/arrow-typeclasses \
 --path arrow-instances \
 --path kategory \
 --path kats \
 --path katz \
 --path library \
 --path arrow-free \
 --path modules/core/arrow-annotations \
 --path modules/core/arrow-core-data \
 --path modules/core/arrow-extras-extensions

git remote add upstream git@github.com:arrow-kt/arrow-core.git
git push --all upstream

echo "SITE ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-site
cd arrow-site

git-filter-repo --path modules/docs/arrow-docs/docs \
 --path-rename modules/docs/arrow-docs/docs:docs \
 --path arrow-blog-section \
 --path arrow-version \
 --path arrow-docs \
 --path modules/talks \
 --path kategory-docs

git remote add upstream git@github.com:arrow-kt/arrow-site.git
git push --all upstream

echo "DOCS ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-docs
cd arrow-docs

git-filter-repo --path modules/docs/arrow-docs \
 --path modules/docs/arrow-examples \
 --path-rename modules/docs/arrow-docs:arrow-docs \
 --path-rename modules/docs/arrow-examples:arrow-examples \
 --path arrow-docs \
 --path arrow-examples \
 --path kategory-docs \
 --path arrow-blog-section \
 --path modules/talks \
 --path arrow-version \
 --path modules/docs/arrow-docs \
 --path modules/docs/arrow-examples

git remote add upstream git@github.com:arrow-kt/arrow-docs.git
git push --all upstream

echo "INTEGRATIONS ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-integrations
cd arrow-integrations

git-filter-repo --path modules/integrations/arrow-integrations-retrofit-adapter \
 --path modules/integrations/arrow-integrations-jackson-module \
 --path-rename modules/integrations/arrow-integrations-retrofit-adapter:arrow-integrations-retrofit-adapter \
 --path-rename modules/integrations/arrow-integrations-jackson-module:arrow-integrations-jackson-module

git remote add upstream git@github.com:arrow-kt/arrow-integrations.git
git push --all upstream

echo "FX ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-fx
cd arrow-fx

git-filter-repo --path modules/fx/arrow-fx \
 --path modules/fx/arrow-fx-reactor \
 --path modules/fx/arrow-fx-rx2 \
 --path modules/benchmarks/arrow-benchmarks-fx \
 --path modules/streams/arrow-streams \
 --path modules/fx/arrow-fx-kotlinx-coroutines \
 --path modules/benchmarks/arrow-kio-benchmarks \
 --path modules/benchmarks/arrow-scala-benchmarks \
 --path-rename modules/fx/arrow-fx:arrow-fx \
 --path-rename modules/fx/arrow-fx-reactor:arrow-fx-reactor \
 --path-rename modules/fx/arrow-fx-rx2:arrow-fx-rx2 \
 --path-rename modules/benchmarks/arrow-benchmarks-fx:arrow-benchmarks-fx \
 --path-rename modules/streams/arrow-streams:arrow-streams \
 --path-rename modules/fx/arrow-fx-kotlinx-coroutines:arrow-fx-kotlinx-coroutines \
 --path-rename modules/benchmarks/arrow-scala-benchmarks:arrow-benchmarks-fx/arrow-scala-benchmarks \
 --path-rename modules/benchmarks/arrow-kio-benchmarks:arrow-benchmarks-fx/arrow-kio-benchmarks \
 --path modules/effects \
 --path modules/benchmarks/arrow-benchmarks-effects \
 --path modules/streams \
 --path arrow-effects-kotlinx-coroutines \
 --path arrow-effects-rx2 \
 --path arrow-effects-test \
 --path kategory-effects-rx2 \
 --path kategory-effects \
 --path arrow-effects

git remote add upstream git@github.com:arrow-kt/arrow-fx.git
git push --all upstream

echo "INCUBATOR ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-incubator
cd arrow-incubator

git-filter-repo --path modules/aql/arrow-aql \
 --path modules/mtl/arrow-mtl \
 --path modules/mtl/arrow-mtl-data \
 --path modules/fx/arrow-fx-mtl \
 --path modules/recursion/arrow-recursion \
 --path modules/recursion/arrow-recursion-data \
 --path modules/core/arrow-generic \
 --path modules/free/arrow-free \
 --path modules/free/arrow-free-data \
 --path modules/reflect/arrow-reflect \
 --path modules/core/arrow-kindedj \
 --path modules/validation/arrow-validation \
 --path-rename modules/aql/arrow-aql:arrow-aql \
 --path-rename modules/mtl/arrow-mtl:arrow-mtl \
 --path-rename modules/mtl/arrow-mtl-data:arrow-mtl-data \
 --path-rename modules/fx/arrow-fx-mtl:arrow-fx-mtl \
 --path-rename modules/recursion/arrow-recursion:arrow-recursion \
 --path-rename modules/recursion/arrow-recursion-data:arrow-recursion-data \
 --path-rename modules/core/arrow-generic:arrow-generic \
 --path-rename modules/free/arrow-free:arrow-free \
 --path-rename modules/free/arrow-free-data:arrow-free-data \
 --path-rename modules/reflect/arrow-reflect:arrow-reflect \
 --path-rename modules/core/arrow-kindedj:arrow-kindedj \
 --path-rename modules/validation/arrow-validation:arrow-validation \
 --path arrow-free \
 --path arrow-generic \
 --path arrow-kindedj \
 --path arrow-mtl \
 --path arrow-data \
 --path arrow-recursion \
 --path modules/recursion-schemes \
 --path modules/recursion-schemes \
 --path modules/recursion \
 --path modules/core/arrow-validation \
 --path modules/free \
 --path arrow-validation \
 --path arrow-typeclasses \
 --path modules/core/arrow-extras-extensions \
 --path funktionale-validation \
 --path kategory-kindedj \
 --path modules/aql/arrow-query-language \
 --path modules/core/arrow-extras \
 --path modules/core/arrow-mtl

git remote add upstream git@github.com:arrow-kt/arrow-incubator.git
git push --all upstream

echo "OPTICS ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-optics
cd arrow-optics

git-filter-repo --path modules/optics/arrow-optics \
 --path modules/optics/arrow-optics-mtl \
 --path-rename modules/optics/arrow-optics:arrow-optics \
 --path-rename modules/optics/arrow-optics-mtl:arrow-optics-mtl \
 --path arrow-optics \
 --path kategory-optics \
 --path modules/optics/arrow-optics

git remote add upstream git@github.com:arrow-kt/arrow-optics.git
git push --all upstream

echo "TEST ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-test
cd arrow-test

git-filter-repo --path modules/core/arrow-test \
 --path-rename modules/core/arrow-test:arrow-test \
 --path arrow-test \
 --path arrow-effects-test

git remote add upstream git@github.com:arrow-kt/arrow-test.git
git push --all upstream

echo "UI ..."
cd $BASEDIR
git clone git@github.com:arrow-kt/arrow.git arrow-ui
cd arrow-ui

git-filter-repo --path modules/ui/arrow-ui \
 --path modules/ui/arrow-ui-data \
 --path-rename modules/ui/arrow-ui:arrow-ui \
 --path-rename modules/ui/arrow-ui-data:arrow-ui-data \
 --path modules/core/arrow-data \
 --path modules/core/arrow-extras-extensions \
 --path modules/core/arrow-extras

git remote add upstream git@github.com:arrow-kt/arrow-ui.git
git push --all upstream

