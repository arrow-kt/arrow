# How to move a pending PR from arrow repository to a new repository

## Table of contents

* [The branch is already pushed in arrow repository](#The-branch-is-already-pushed-in-arrow-repository)
* [The branch is located in a forked repository of your personal account](#The-branch-is-located-in-a-forked-repository-of-your-personal-account)
* [The branch is still in a local workspace from arrow repository](#The-branch-is-still-in-a-local-workspace-from-arrow-repository)

## The branch is already pushed in arrow repository

If your branch is already pushed in arrow repository, it's not necessary to do anything beyond creating the pull request again in the correspondent repository because branches are also moved to the new repositories automatically.

## The branch is located in a forked repository of your personal account

### Note

Please, ping `@Rachel` in [kotlinlang Slack](https://kotlinlang.slack.com) in case your dev environment is not ready to follow these steps.

She can move the pull request for you!

### Prerequisites

* git >= 2.24.0
* python3 >= 3.5
* `git-filter-repo`
    * Download from [git-filter-repo in GitHub](https://github.com/newren/git-filter-repo/blob/master/git-filter-repo)
    * Add its location to `PATH`
    * Note: it's recommended by Git instead of [git-filter-branch](https://git-scm.com/docs/git-filter-branch)

### Commands

Check the content for the new repositories in [repositories organization](repositories-organization.md).

#### Moving the PR to arrow-core repository

From your Git worspace:

```
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

git push origin <branch-name>
```

and create the pull request to **arrow-core** repository.

#### Moving the PR to arrow-fx repository

From your Git worspace:

```
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

git push origin <branch-name>
```

and create the pull request to **arrow-fx** repository.

### Moving the PR to arrow-ank repository

From your Git worspace:

```
git-filter-repo --path modules/ank/arrow-ank \
 --path modules/ank/arrow-ank-gradle \
 --path-rename modules/ank/arrow-ank:arrow-ank \
 --path-rename modules/ank/arrow-ank-gradle:arrow-ank-gradle \
 --path ank-core

git push origin <branch-name>
```

and create the pull request to **arrow-ank** repository.

### Moving the PR to arrow-incubator repository

From your Git worspace:

```
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

git push origin <branch-name>
```

and create the pull request to **arrow-incubator** repository.

### Moving the PR to arrow-docs repository

From your Git worspace:

```
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

git push origin <branch-name>
```

and create the pull request to **arrow-docs** repository.

### Moving the PR to arrow-integrations repository

From your Git worspace:

```
git-filter-repo --path modules/integrations/arrow-integrations-retrofit-adapter \
 --path modules/integrations/arrow-integrations-jackson-module \
 --path-rename modules/integrations/arrow-integrations-retrofit-adapter:arrow-integrations-retrofit-adapter \
 --path-rename modules/integrations/arrow-integrations-jackson-module:arrow-integrations-jackson-module

git push origin <branch-name>
```

and create the pull request to **arrow-integrations** repository.

### Moving the PR to arrow-ui repository

From your Git worspace:

```
git-filter-repo --path modules/ui/arrow-ui \
 --path modules/ui/arrow-ui-data \
 --path-rename modules/ui/arrow-ui:arrow-ui \
 --path-rename modules/ui/arrow-ui-data:arrow-ui-data \
 --path modules/core/arrow-data \
 --path modules/core/arrow-extras-extensions \
 --path modules/core/arrow-extras

git push origin <branch-name>
```

and create the pull request to **arrow-ui** repository.

### Moving the PR to arrow-optics repository

From your Git worspace:

```
git-filter-repo --path modules/optics/arrow-optics \
 --path modules/optics/arrow-optics-mtl \
 --path-rename modules/optics/arrow-optics:arrow-optics \
 --path-rename modules/optics/arrow-optics-mtl:arrow-optics-mtl \
 --path arrow-optics \
 --path kategory-optics \
 --path modules/optics/arrow-optics

git push origin <branch-name>
```

and create the pull request to **arrow-optics** repository.

### Moving the PR to arrow-test repository

From your Git worspace:

```
git-filter-repo --path modules/core/arrow-test \
 --path-rename modules/core/arrow-test:arrow-test \
 --path arrow-test \
 --path arrow-effects-test

git push origin <branch-name>
```

and create the pull request to **arrow-test** repository.

### Moving the PR to arrow-site repository

From your Git worspace:

```
git-filter-repo --path modules/docs/arrow-docs/docs \
 --path-rename modules/docs/arrow-docs/docs:docs \
 --path arrow-blog-section \
 --path arrow-version \
 --path arrow-docs \
 --path modules/talks \
 --path kategory-docs

git push origin <branch-name>
```

and create the pull request to **arrow-site** repository.

## The branch is still in a local workspace from arrow repository

Situation: you can push branches in **arrow** repository so far and your work-in-progress branch is still in a local workspace from arrow repository.

Please, if you read this documentation before doing the re-org, push your branch to be included automatically.

Otherwise, run the `git-filter-repo` command from the previous section (just the `git-filter-repo` command). 

Then:

```
git remote add upstream git@github.com:arrow-kt/arrow-<library>.git

git push upstream <branch-name>
```

and create the pull request in the correspondent repository.
