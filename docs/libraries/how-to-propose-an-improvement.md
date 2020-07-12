# Arrow library: How to propose an improvement

If it's the first time you contribute with a GitHub repository, take a look at [Collaborating with issues and pull requests](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests).

Does your improvement impact on several repositories? Please, use the same branch name in that case.

## What happens when creating a pull request?

When creating a pull request, several actions are executed automatically to check that pull request:

* Required checks
  * `arrow library: build`:
    * Install the Arrow library that's being improved in the local Maven repository.
    * Install other Arrow libraries that have related changes (there are open pull requests with the same branch name). The rest of the Arrow libraries will be downloaded from the [OSS repository](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/).
    * Run `check` Gradle task for the Arrow library that's being improved.
  * `arrow library: build documentation`:
    * Install the Arrow library that's being improved in the local Maven repository.
    * Install other Arrow libraries that have related changes (there are open pull requests with the same branch name). The rest of the Arrow libraries will be downloaded from the [OSS repository](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/).
    * Run `buildArrowDoc` Gradle task for the Arrow library that's being improved.

* Optional checks
  * `Check Previous Build Integration`:
    * Install all the Arrow libraries in the local Maven repository (open pull requests with the same branch name will be considered).
    * Run `check` Gradle task for all the Arrow libraries.
  * `Check Previous Doc Integration`:
    * Install all the Arrow libraries in the local Maven repository (open pull requests with the same branch name will be considered).
    * Run `buildArrowDoc` Gradle task for all the Arrow libraries.

Besides the required checks, it's necessary to get the approval by one of the members of the Arrow Community.

## What happens when merging a pull request?

When merging the pull request, a new SNAPSHOT library will be published into [OSS repository](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/).

On the other hand, the documentation for the next version (SNAPSHOT) will be updated:

* [Arrow Core](https://arrow-kt.io/docs/next/core/)
* [Arrow Fx](https://arrow-kt.io/docs/next/fx/)
* [Arrow Optics](https://arrow-kt.io/docs/next/optics/dsl/)
* [Arrow Incubator](https://arrow-kt.io/docs/next/aql/intro/)

And global checks will be executed again.

Please, if there are several related pull requests, merge them at the same time (one after the other). All these actions will wait for a few minutes to be started and to get the last content from `master` branch for all the repositories.

If any of these actions fails, an issue will be created to be solved as soon as possible.
